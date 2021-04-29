package com.flipkart.gjex.grpc.interceptor;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.ResponseMetered;
import com.flipkart.gjex.core.util.Pair;
import com.flipkart.gjex.grpc.utils.AnnotationUtils;
import com.google.inject.Inject;
import io.grpc.BindableService;
import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatusMetricInterceptor implements ServerInterceptor {

    private final MetricRegistry metricRegistry;
    private final Set<String> meteredMethods;

    @Inject
    public StatusMetricInterceptor(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
        this.meteredMethods = new HashSet<>();
    }

    public void registerMeteredMethods(List<BindableService> bindableServices) {
        bindableServices.forEach(service -> {
            List<Pair<?, Method>> annotatedMethods = AnnotationUtils.getAnnotatedMethods(service.getClass(), ResponseMetered.class);
            if (annotatedMethods != null) {
                annotatedMethods.forEach(pair -> {
                    String methodName = service.bindService().getServiceDescriptor().getName() + "/" + pair.getValue().getName();
                    meteredMethods.add(methodName.toLowerCase());
                });
            }
        });
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> next) {
        String methodName = call.getMethodDescriptor().getFullMethodName();
        if (!meteredMethods.contains(methodName.toLowerCase())) {
            return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            }, metadata);
        }
        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void close(Status status, Metadata trailers) {
                metricRegistry.meter(methodName + "." + status.getCode().name()).mark();
                super.close(status, trailers);
            }
        }, metadata);
    }

}
