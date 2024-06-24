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
import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.filter.MethodFilters;
import com.flipkart.gjex.core.filter.ServerRequestParams;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.util.Pair;
import com.flipkart.gjex.grpc.utils.AnnotationUtils;
import com.google.protobuf.GeneratedMessageV3;
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
import io.grpc.StatusRuntimeException;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An implementation of the gRPC {@link ServerInterceptor} that allows custom {@link Filter} instances to be invoked around relevant methods to process Request, Request-Headers, Response and
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
    private Map<String, List<Filter>> filtersMap = new HashMap<String, List<Filter>>();

    @SuppressWarnings("rawtypes")
    public void registerFilters(List<Filter> filters, List<BindableService> services) {
        Map<Class<?>, Filter> classToInstanceMap = filters.stream()
                .collect(Collectors.toMap(Object::getClass, Filter::getInstance));
        services.forEach(service -> {
            List<Pair<?, Method>> annotatedMethods = AnnotationUtils.getAnnotatedMethods(service.getClass(), MethodFilters.class);
            if (annotatedMethods != null) {
                annotatedMethods.forEach(pair -> {
                    List<Filter> filtersForMethod = new LinkedList<Filter>();
                    Arrays.asList(pair.getValue().getAnnotation(MethodFilters.class).value()).forEach(filterClass -> {
                        if (!classToInstanceMap.containsKey(filterClass)) {
                            throw new RuntimeException("Filter instance not bound for Filter class :" + filterClass.getName());
                        }
                        filtersForMethod.add(classToInstanceMap.get(filterClass));
                    });
                    // Key is of the form <Service Name>+ "/" +<Method Name>
                    // reflecting the structure followed in the gRPC HandlerRegistry using MethodDescriptor#getFullMethodName()
                    filtersMap.put((service.bindService().getServiceDescriptor().getName() + "/" + pair.getValue().getName()).toLowerCase(),
                            filtersForMethod);
                });
            }
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        List<Filter> filters = filtersMap.get(call.getMethodDescriptor().getFullMethodName().toLowerCase());
        Metadata forwardHeaders = new Metadata();
        if (filters == null) {
            return new SimpleForwardingServerCallListener<ReqT>(next.startCall(
                    new SimpleForwardingServerCall<ReqT, RespT>(call) {
                    }, headers)) {
            };
        }
        for (Filter filter : filters) {
            try {
                ServerRequestParams serverRequestParams = new ServerRequestParams(Objects.requireNonNull(call.getAttributes()
                    .get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)).toString(),
                    call.getMethodDescriptor().getFullMethodName().toLowerCase());
                filter.doFilterRequest(serverRequestParams,headers);
                for (Metadata.Key key : filter.getForwardHeaderKeys()) {
                    Object value = headers.get(key);
                    if (value != null) {
                        forwardHeaders.put(key, value);
                    }
                }
            } catch (StatusRuntimeException se) {
                call.close(se.getStatus(), se.getTrailers()); // Closing the call and not letting it to proceed further
                return new ServerCall.Listener<ReqT>() {
                };
            }
        }

        Context contextWithHeaders = forwardHeaders.keys().isEmpty() ? null : Context.current().withValue(GJEXContext.getHeadersKey(), forwardHeaders);

        ServerCall.Listener<ReqT> listener = null;
        listener = next.startCall(new SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void sendMessage(final RespT response) {
                Context previous = attachContext(contextWithHeaders);   // attaching headers to gRPC context
                try {
                    filters.forEach(filter -> filter.doProcessResponse((GeneratedMessageV3) response));
                    super.sendMessage(response);
                } finally {
                    detachContext(contextWithHeaders, previous);    // detach headers from gRPC context
                }
            }

            @Override
            public void sendHeaders(final Metadata responseHeaders) {
                Context previous = attachContext(contextWithHeaders);   // attaching headers to gRPC context
                try {
                    filters.forEach(filter -> filter.doProcessResponseHeaders(responseHeaders));
                    super.sendHeaders(responseHeaders);
                } finally {
                    detachContext(contextWithHeaders, previous);    // detach headers from gRPC context
                }
            }
        }, headers);

        return new SimpleForwardingServerCallListener<ReqT>(listener) {
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
            public void onMessage(ReqT request) {
                Context previous = attachContext(contextWithHeaders);   // attaching headers to gRPC context
                try {
                    filters.forEach(filter -> filter.doProcessRequest((GeneratedMessageV3) request));
                    super.onMessage(request);
                } finally {
                    detachContext(contextWithHeaders, previous);    // detach headers from gRPC context
                }
            }
        };
    }

    /**
     * Helper method to handle RuntimeExceptions and convert it into suitable gRPC message. Closes the ServerCall
     */
    private <ReqT, RespT> void handleException(ServerCall<ReqT, RespT> call, Exception e) {
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
