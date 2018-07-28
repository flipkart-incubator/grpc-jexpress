package com.flipkart.gjex.grpc.channel;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.grpc.*;


import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Created by rohit.k on 28/07/18.
 */
public class InstrumentedChannel extends Channel {
    @Inject
    private MetricRegistry metricRegistry;

    private final GrpcChannelConfig channelConfig;
    private final ManagedChannel _delegate ;

    @Inject
    public InstrumentedChannel(GrpcChannelConfig channelConfig){

        this.channelConfig = channelConfig;
        _delegate = ManagedChannelBuilder.forAddress(channelConfig.getHostname(),channelConfig.getPort()).usePlaintext(true).build();
        //TODO remove this line
        metricRegistry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.SECONDS);

    }
    @Override
    public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(MethodDescriptor<RequestT, ResponseT> methodDescriptor, CallOptions callOptions) {

        Timer.Context clientTimer = metricRegistry.timer(getMetricName(methodDescriptor.getFullMethodName())).time();
        ClientCall<RequestT, ResponseT> response ;
        try {
            response= _delegate.newCall(methodDescriptor, callOptions);
        }finally {
            clientTimer.stop();
        }

        return response;
    }

    @Override
    public String authority() {
        return _delegate.authority();
    }

    private String getMetricName(String fullMethodName){
        return fullMethodName.replace("/", ".");
    }
//    public static void main(String[] args) throws InterruptedException {
//        GrpcChannelConfig channelConfig = new GrpcChannelConfig("localhost",9999);
//        GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(new InstrumentedChannel(channelConfig));
//        blockingStub.sayHello(HelloRequest.getDefaultInstance());
//        Thread.sleep(10000);
//
//    }
}

