/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flipkart.gjex.grpc.interceptor;

import com.flipkart.gjex.core.context.GJEXContext;
import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.grpc.GrpcFilter;
import com.flipkart.gjex.core.filter.grpc.GrpcFilterConfig;
import com.flipkart.gjex.core.filter.grpc.MethodFilters;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.util.NetworkUtils;
import com.flipkart.gjex.core.util.Pair;
import com.flipkart.gjex.grpc.utils.AnnotationUtils;
import io.grpc.BindableService;
import io.grpc.Context;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Grpc;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An implementation of the gRPC {@link ServerInterceptor} that allows custom {@link GrpcFilter} instances to be invoked around relevant methods to process Request, Request-Headers, Response and
 * Response-Headers data.
 *
 * @author regu.b
 */
@Singleton
@Named("FilterInterceptor")
public class FilterInterceptor implements ServerInterceptor, Logging {

    /**
     * Map of Filter instances mapped to Service and its method
     */
    @SuppressWarnings("rawtypes")
    private final Map<String, List<GrpcFilter>> filtersMap = new HashMap<>();

    @SuppressWarnings("rawtypes")
    public void registerFilters(List<GrpcFilter> grpcFilters, List<BindableService> services,
                                GrpcFilterConfig grpcFilterConfig) {
        Map<Class<?>, GrpcFilter<?,?>> classToInstanceMap = grpcFilters.stream()
                .collect(Collectors.<GrpcFilter, Class<?>, GrpcFilter<?, ?>>toMap(
                    GrpcFilter::getClass,
                    filter -> filter,
                    (existing, replacement) -> existing
                ));
        services.forEach(service -> {
            List<Pair<?, Method>> annotatedMethods = AnnotationUtils.getAnnotatedMethods(service.getClass(), MethodFilters.class);
            if (annotatedMethods != null) {
                annotatedMethods.forEach(pair -> {
                    List<GrpcFilter> filtersForMethod = new ArrayList<>();
                    try {
                        filtersForMethod.addAll(addAllStaticFilters(grpcFilterConfig, classToInstanceMap));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("Failed to load filter class: " + e.getMessage(), e);
                    }
                    Arrays.asList(pair.getValue().getAnnotation(MethodFilters.class).value()).forEach(filterClass -> {
                        if (!classToInstanceMap.containsKey(filterClass)) {
                            throw new RuntimeException("Filter instance not bound for Filter class :" + filterClass.getName());
                        }
                        GrpcFilter grpcFilter = classToInstanceMap.get(filterClass).configure(grpcFilterConfig);
                        if (grpcFilter != null && filtersForMethod.stream().noneMatch(existing -> existing.getClass().equals(grpcFilter.getClass()))) {
                            filtersForMethod.add(grpcFilter);
                        }
                    });
                    // Key is of the form <Service Name>+ "/" +<Method Name>
                    // reflecting the structure followed in the gRPC HandlerRegistry using MethodDescriptor#getFullMethodName()
                    String methodSignature =
                        (service.bindService().getServiceDescriptor().getName() + "/" + pair.getValue().getName()).toLowerCase();
                    filtersMap.put(methodSignature, filtersForMethod);
                });
            }
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <Req, Res> Listener<Req> interceptCall(ServerCall<Req, Res> call, Metadata headers, ServerCallHandler<Req, Res> next) {
        List<GrpcFilter> grpcFilterReferences = filtersMap.get(call.getMethodDescriptor().getFullMethodName().toLowerCase());
        Metadata forwardHeaders = new Metadata();
        if (grpcFilterReferences == null || grpcFilterReferences.isEmpty()){
            return new SimpleForwardingServerCallListener<Req>(next.startCall(
                new SimpleForwardingServerCall<Req, Res>(call) {
                }, headers)) {
            };
        }
        List<GrpcFilter> grpcFilters = grpcFilterReferences.stream().map(GrpcFilter::getInstance).collect(Collectors.toList());

        for (GrpcFilter filter : grpcFilters) {
            for (Metadata.Key key : filter.getForwardHeaderKeys()) {
                Object value = headers.get(key);
                if (value != null) {
                    forwardHeaders.put(key, value);
                }
            }
        }

        Context contextWithHeaders = forwardHeaders.keys().isEmpty() ? null : Context.current().withValue(GJEXContext.getHeadersKey(), forwardHeaders);

        ServerCall.Listener<Req> listener = next.startCall(new SimpleForwardingServerCall<Req, Res>(call) {
            @Override
            public void sendMessage(final Res response) {
                Context previous = attachContext(contextWithHeaders);   // attaching headers to gRPC context
                try {
                    grpcFilters.forEach(filter -> filter.doProcessResponse(response));
                    super.sendMessage(response);
                } finally {
                    detachContext(contextWithHeaders, previous);    // detach headers from gRPC context
                }
            }

            @Override
            public void sendHeaders(final Metadata responseHeaders) {
                Context previous = attachContext(contextWithHeaders);   // attaching headers to gRPC context
                try {
                    grpcFilters.forEach(filter -> filter.doProcessResponseHeaders(responseHeaders));
                    super.sendHeaders(responseHeaders);
                } finally {
                    detachContext(contextWithHeaders, previous);    // detach headers from gRPC context
                }
            }
        }, headers);

        RequestParams requestParams = RequestParams.builder()
                .clientIp(getClientIp(call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)))
                .resourcePath(call.getMethodDescriptor().getFullMethodName().toLowerCase())
                .method(call.getMethodDescriptor().getType().name())
                .metadata(headers)
                .build();

        return new SimpleForwardingServerCallListener<Req>(listener) {
            @Override
            public void onHalfClose() {
                Context previous = attachContext(contextWithHeaders);   // attaching headers to gRPC context
                try {
                    super.onHalfClose();
                } catch (RuntimeException ex) {
                    handleException(call, ex);
                    grpcFilters.forEach(filter -> filter.doHandleException(ex));
                } finally {
                    detachContext(contextWithHeaders, previous);    // detach headers from gRPC context
                }
            }

            @Override
            public void onMessage(Req request) {
                Context previous = attachContext(contextWithHeaders);   // attaching headers to gRPC context
                try {

                    for (GrpcFilter filter : grpcFilters) {
                        filter.doProcessRequest(request, requestParams);
                    }
                    super.onMessage(request);
                } catch (StatusException ex) {
                    handleException(call, ex);
                    grpcFilters.forEach(filter -> filter.doHandleException(ex));
                } finally {
                    detachContext(contextWithHeaders, previous);    // detach headers from gRPC context
                }
            }

            @Override
            public void onCancel() {
                Context previous = attachContext(contextWithHeaders);   // attaching headers to gRPC context
                try {
                    super.onCancel();
                } catch (RuntimeException ex) {
                    handleException(call, ex);
                    grpcFilters.forEach(filter -> filter.doHandleException(ex));
                } finally {
                    detachContext(contextWithHeaders, previous);    // detach headers from gRPC context
                }
            }
        };
    }

    /**
     * Helper method to handle RuntimeExceptions and convert it into suitable gRPC message. Closes the ServerCall
     */
    private <Req, Res> void handleException(ServerCall<Req, Res> call, Exception e) {
        error("Closing gRPC call due to RuntimeException.", e);
        Status returnStatus = Status.INTERNAL;
        Metadata metadata = new Metadata();

        if (e instanceof StatusRuntimeException){
            StatusRuntimeException statusRuntimeException = (StatusRuntimeException) e;
            returnStatus = statusRuntimeException.getStatus();
            if (statusRuntimeException.getTrailers() != null) {
                metadata = statusRuntimeException.getTrailers();
            }
        } else if (e instanceof StatusException){
            StatusException statusException = (StatusException) e;
            returnStatus = statusException.getStatus();
            if (statusException.getTrailers() != null) {
                metadata = statusException.getTrailers();
            }
        }

        try {
            call.close(returnStatus.withDescription(e.getMessage()), metadata);
        } catch (IllegalStateException ie) {
            // Simply log the exception as this is already handling the runtime-exception
            warn("Exception while attempting to close ServerCall stream: " + ie.getMessage());
        }
    }

    /* Helper method to attach a context */
    private Context attachContext(Context context) {
        return context == null ? null : context.attach();
    }

    /* Helper method to detach previous context */
    private void detachContext(Context currentContext, Context previousContext) {
        if (currentContext != null) {
            currentContext.detach(previousContext);
        }
    }

    private List<GrpcFilter<?,?>> addAllStaticFilters(GrpcFilterConfig grpcFilterConfig,  Map<Class<?>, GrpcFilter<?,?>> classToInstanceMap) throws ClassNotFoundException {
        List<String> filterClasses = grpcFilterConfig.getGlobalFilterClasses();
        List<GrpcFilter<?,?>> filtersForMethod = new ArrayList<>();

        if (CollectionUtils.isEmpty(filterClasses)) {
            return filtersForMethod;
        }

        for (String filterClass : filterClasses) {
            Class<?> clazz = Class.forName(filterClass); // This will throw ClassNotFoundException if needed
            if (classToInstanceMap.containsKey(clazz)) {
                GrpcFilter<?,?> filter = classToInstanceMap.get(clazz).configure(grpcFilterConfig);
                if (filter != null && filtersForMethod.stream().noneMatch(existing -> existing.getClass().equals(filter.getClass()))) {
                    filtersForMethod.add(filter);
                }
            }
        }

        return filtersForMethod;
    }

    protected static String getClientIp(SocketAddress socketAddress) {
        if (socketAddress != null) {
            if (socketAddress instanceof InetSocketAddress) {
                return ((InetSocketAddress)socketAddress).getHostName();
            } else {
                // handle other scenarios use regex
                String socketAddressString = socketAddress.toString();
                return NetworkUtils.extractIPAddress(socketAddressString);
            }
        }
        return "0.0.0.0";
    }


}
