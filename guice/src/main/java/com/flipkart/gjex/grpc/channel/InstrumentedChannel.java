package com.flipkart.gjex.grpc.channel;

;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.grpc.*;
import javax.inject.Inject;


/**
 * Created by rohit.k on 28/07/18.
 */

/**
 * A wrapper implementation over @{@link ManagedChannel} which provides metrics of external calls
 */
public class InstrumentedChannel extends Channel {
    @Inject
    private MetricRegistry metricRegistry;

    private final ChannelConfig channelConfig;
    private final ManagedChannel _delegate ;

    @Inject
    public InstrumentedChannel(ChannelConfig channelConfig){

        this.channelConfig = channelConfig;
        _delegate = ManagedChannelBuilder.forAddress(channelConfig.getHostname(),channelConfig.getPort()).usePlaintext(true).build();

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
}

