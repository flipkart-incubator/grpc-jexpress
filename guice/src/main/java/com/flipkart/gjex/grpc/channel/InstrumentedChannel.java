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
package com.flipkart.gjex.grpc.channel;

import javax.inject.Inject;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.MethodDescriptor;


/**
 * Created by rohit.k on 28/07/18.
 */

/**
 * A wrapper implementation over @{@link ManagedChannel} which provides metrics of external calls
 */
public class InstrumentedChannel extends Channel {
    @Inject
    private MetricRegistry metricRegistry;

    private final ManagedChannel _delegate ;

    @Inject
    public InstrumentedChannel(ChannelConfig channelConfig){
        _delegate = ManagedChannelBuilder.forAddress(channelConfig.getHostname(),channelConfig.getPort()).build();
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

