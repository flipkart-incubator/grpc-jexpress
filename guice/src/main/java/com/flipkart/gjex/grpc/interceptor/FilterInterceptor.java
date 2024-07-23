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
import com.flipkart.gjex.core.filter.ResponseParams;
import com.flipkart.gjex.core.filter.grpc.GjexGrpcFilter;
import com.flipkart.gjex.core.filter.grpc.GrpcAccessLogGjexGrpcFilter;
import com.flipkart.gjex.core.filter.grpc.MethodFilters;
import com.flipkart.gjex.core.logging.Logging;
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

import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An implementation of the gRPC {@link ServerInterceptor} that allows custom {@link GjexGrpcFilter} instances to be invoked around relevant methods to process Request, Request-Headers, Response and
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
    private final Map<String, List<GjexGrpcFilter>> filtersMap = new HashMap<>();

    @SuppressWarnings("rawtypes")
    public void registerFilters(List<GjexGrpcFilter> grpcFilters, List<BindableService> services,
                                boolean enableAccessLogs) {
        Map<Class<?>, GjexGrpcFilter> classToInstanceMap = grpcFilters.stream()
                .collect(Collectors.toMap(Object::getClass, Function.identity()));
        services.forEach(service -> {
            List<Pair<?, Method>> annotatedMethods = AnnotationUtils.getAnnotatedMethods(service.getClass(), MethodFilters.class);
            if (annotatedMethods != null) {
                annotatedMethods.forEach(pair -> {
                    List<GjexGrpcFilter> filtersForMethod = new ArrayList<>();
                    Arrays.asList(pair.getValue().getAnnotation(MethodFilters.class).value()).forEach(filterClass -> {
                        if (!classToInstanceMap.containsKey(filterClass)) {
                            throw new RuntimeException("Filter instance not bound for Filter class :" + filterClass.getName());
                        }
                        filtersForMethod.add(classToInstanceMap.get(filterClass));
                    });
                    // Key is of the form <Service Name>+ "/" +<Method Name>
                    // reflecting the structure followed in the gRPC HandlerRegistry using MethodDescriptor#getFullMethodName()
                    String methodSignature =
                        (service.bindService().getServiceDescriptor().getName() + "/" + pair.getValue().getName()).toLowerCase();
                    filtersMap.put(methodSignature, filtersForMethod);
                    if (enableAccessLogs){
                        filtersMap.get(methodSignature).add(new GrpcAccessLogGjexGrpcFilter());
                    }
                });
            }
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <Req, Res> Listener<Req> interceptCall(ServerCall<Req, Res> call, Metadata headers, ServerCallHandler<Req, Res> next) {
        List<GjexGrpcFilter> grpcFilterReferences =
            filtersMap.get(call.getMethodDescriptor().getFullMethodName().toLowerCase());
        Metadata forwardHeaders = new Metadata();
        if (grpcFilterReferences == null || grpcFilterReferences.isEmpty()){
            return new SimpleForwardingServerCallListener<Req>(next.startCall(
                new SimpleForwardingServerCall<Req, Res>(call) {
                }, headers)) {
            };
        }
        List<GjexGrpcFilter> grpcFilters = grpcFilterReferences.stream().map(GjexGrpcFilter::getInstance).collect(Collectors.toList());
        Context contextWithHeaders = forwardHeaders.keys().isEmpty() ? null : Context.current().withValue(GJEXContext.getHeadersKey(), forwardHeaders);

        ServerCall.Listener<Req> listener = null;
        listener = next.startCall(new SimpleForwardingServerCall<Req, Res>(call) {
            @Override
            public void sendMessage(final Res response) {
                Context previous = attachContext(contextWithHeaders);   // attaching headers to gRPC context
                try {
                    grpcFilters.forEach(filter -> filter.doProcessResponse(ResponseParams.builder().response(response).build()));
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

        return new SimpleForwardingServerCallListener<Req>(listener) {
            @Override
            public void onHalfClose() {
                Context previous = attachContext(contextWithHeaders);   // attaching headers to gRPC context
                try {
                    super.onHalfClose();
                } catch (RuntimeException ex) {
                    handleException(call, ex);
                } finally {
                    detachContext(contextWithHeaders, previous);    // detach headers from gRPC context
                }
            }

            @Override
            public void onMessage(Req request) {
                Context previous = attachContext(contextWithHeaders);   // attaching headers to gRPC context
                try {
                    for (GjexGrpcFilter grpcFilter : grpcFilters) {
                        if (grpcFilter != null) {
                            RequestParams requestParams =
                                RequestParams.builder()
                                    .clientIp(call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR).toString())
                                    .resourcePath(call.getMethodDescriptor().getFullMethodName().toLowerCase())
                                    .metadata(headers)
                                    .request(request)
                                    .build();
                            grpcFilters.forEach(filter -> filter.doProcessRequest(requestParams));
                            for (Metadata.Key key : grpcFilter.getForwardHeaderKeys()) {
                                Object value = headers.get(key);
                                if (value != null) {
                                    forwardHeaders.put(key, value);
                                }
                            }
                        }
                    }
                    super.onMessage(request);
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
        if (ConstraintViolationException.class.isAssignableFrom(e.getClass())) {
            returnStatus = Status.INVALID_ARGUMENT;
        }

        try {
            call.close(returnStatus.withDescription(e.getMessage()), new Metadata());
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

}
