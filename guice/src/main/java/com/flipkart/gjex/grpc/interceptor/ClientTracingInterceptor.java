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

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.flipkart.gjex.core.tracing.ActiveSpanSource;
import com.flipkart.gjex.core.tracing.OperationNameConstructor;
import com.google.common.collect.ImmutableMap;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;

/** 
 * An intercepter that applies tracing via OpenTracing to all client requests, if a Trace is active.
 * Inspired by https://github.com/grpc-ecosystem/grpc-opentracing/blob/master/java/src/main/java/io/opentracing/contrib/ClientTracingInterceptor.java 
 */ 
public class ClientTracingInterceptor implements ClientInterceptor {
    
    private final Tracer tracer;
    private final OperationNameConstructor operationNameConstructor;
    private final ActiveSpanSource activeSpanSource;

    /**
     * @param tracer to use to trace requests
     */
    public ClientTracingInterceptor(Tracer tracer) {
        this.tracer = tracer;
        this.operationNameConstructor = OperationNameConstructor.DEFAULT;
        this.activeSpanSource = ActiveSpanSource.GRPC_CONTEXT;
    }

    /**
     * Use this intercepter to trace requests made by this client channel.
     * @param channel to be traced
     * @return intercepted channel
     */ 
    public Channel intercept(Channel channel) {
        return ClientInterceptors.intercept(channel, this);
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
        MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

        Span activeSpan = this.activeSpanSource.getActiveSpan();
        if (activeSpan != null) {
	    		final String operationName = operationNameConstructor.constructOperationName(method);
	
	        final Span span = createSpanFromParent(activeSpan, operationName);
	
	        if (callOptions.getDeadline() == null) {
	            span.setTag("grpc.deadline_millis", "null");
	        } else {
	            span.setTag("grpc.deadline_millis", callOptions.getDeadline().timeRemaining(TimeUnit.MILLISECONDS));
	        }
	        
	        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

				@Override
				public void start(Listener<RespT> responseListener, Metadata headers) {
					tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new TextMap() {
						@Override
						public void put(String key, String value) {
							Metadata.Key<String> headerKey = Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER);
							headers.put(headerKey, value);
						}
						@Override
						public Iterator<Entry<String, String>> iterator() {
							throw new UnsupportedOperationException(
									"TextMapInjectAdapter should only be used with Tracer.inject()");
						}
					});
					Listener<RespT> tracingResponseListener = new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(
							responseListener) {
						@Override
						public void onClose(Status status, Metadata trailers) {
							span.finish();
							delegate().onClose(status, trailers);
						}
					};
					delegate().start(tracingResponseListener, headers);
				}

				@Override
				public void cancel(@Nullable String message, @Nullable Throwable cause) {
					String errorMessage;
					if (message == null) {
						errorMessage = "Error";
					} else {
						errorMessage = message;
					}
					if (cause == null) {
						span.log(errorMessage);
					} else {
						span.log(ImmutableMap.of(errorMessage, cause.getMessage()));
					}
					delegate().cancel(message, cause);
				}
			};
        }
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)){};
    }
    
    private Span createSpanFromParent(Span parentSpan, String operationName) {
        if (parentSpan == null) {
            return tracer.buildSpan(operationName).start();
        } else {
            return tracer.buildSpan(operationName).asChildOf(parentSpan).start();
        }
    }

}
