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
package com.flipkart.gjex.guice.module;

import com.flipkart.gjex.grpc.channel.ChannelConfig;
import com.flipkart.gjex.grpc.channel.InstrumentedChannel;
import com.flipkart.gjex.grpc.interceptor.ClientTracingInterceptor;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import io.grpc.Channel;
import io.grpc.stub.AbstractStub;
import io.opentracing.Tracer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rohit.k on 28/07/18.
 */

/**
 * {@link ClientModule} is a guice module to get an Instrumented, Traced instance of GRPC client
 *
 * @param <T> type of the required Grpc Service's stub eg: GreeterGrpc.GreeterBlockingStub
 */
public class ClientModule<T extends AbstractStub<T>> extends AbstractModule {
    private final Class<T> clazz;
    private final ChannelConfig channelConfig;

    @Inject @Named("Tracer")
    Tracer tracer;

    public ClientModule(Class<T> clazz, ChannelConfig channelConfig) {
        this.clazz = clazz;
        this.channelConfig = channelConfig;
    }

    @Override
    protected void configure() {
        bind(clazz).toProvider(new StubProvider());
    }

    private class StubProvider implements Provider<T> {
        private Channel channel;

        public StubProvider() {
            channel = new InstrumentedChannel(channelConfig);
            binder().requestInjection(channel);
        }

        @Override
        public T get() {
            try {
                Constructor<T> constructor = clazz.getDeclaredConstructor(Channel.class);
                constructor.setAccessible(true);
                return constructor.newInstance(channel).withDeadlineAfter(channelConfig.getDeadlineInMs(),
                        TimeUnit.MILLISECONDS).withInterceptors(new ClientTracingInterceptor(tracer));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException| NoSuchMethodException e) {
                throw new RuntimeException("Grpc stub class doesn't have a constructor which only takes  'Channel' as parameter", e);
            }
        }
    }

}
