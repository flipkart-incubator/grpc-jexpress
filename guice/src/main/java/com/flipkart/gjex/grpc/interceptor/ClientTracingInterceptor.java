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

import javax.annotation.Nullable;

import io.grpc.*;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;


/** 
 * An intercepter that applies tracing via OpenTracing to all client requests, if a Trace is active.
 * Inspired by https://github.com/grpc-ecosystem/grpc-opentracing/blob/master/java/src/main/java/io/opentracing/contrib/ClientTracingInterceptor.java 
 */ 
public class ClientTracingInterceptor implements ClientInterceptor {
    
    private final Tracer tracer;
	private final TextMapPropagator textFormat;

    /**
     * @param tracer to use to trace requests
     */
    public ClientTracingInterceptor(Tracer tracer, TextMapPropagator textFormat) {
        this.tracer = tracer;
		this.textFormat = textFormat;
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

		SpanBuilder spanBuilder = tracer.spanBuilder(method.getFullMethodName());
		final Span span = spanBuilder.startSpan();

		return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
			@Override
			public void start(Listener<RespT> responseListener, Metadata headers) {
				textFormat.inject(io.opentelemetry.context.Context.current(), headers, new TextMapSetter<Metadata>() {
					@Override
					public void set(Metadata carrier, String key, String value) {
						carrier.put(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), value);
					}
				});

				Listener<RespT> tracingResponseListener = new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(
						responseListener) {
					@Override
					public void onClose(Status status, Metadata trailers) {
						span.end();
						delegate().onClose(status, trailers);
					}
				};
				delegate().start(tracingResponseListener, headers);
			}


			@Override
			public void cancel(@Nullable String message, @Nullable Throwable cause) {
				span.recordException(cause);
				span.end();
				delegate().cancel(message, cause);
			}
		};
	}
}