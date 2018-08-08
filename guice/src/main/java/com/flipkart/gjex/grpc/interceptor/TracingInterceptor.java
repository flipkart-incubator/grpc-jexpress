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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.tracing.ConfigurableTracingSampler;
import com.flipkart.gjex.core.tracing.OpenTracingContextKey;
import com.flipkart.gjex.core.tracing.Traced;
import com.flipkart.gjex.core.tracing.TracingSampler;
import com.flipkart.gjex.grpc.utils.AnnotationUtils;

import io.grpc.BindableService;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;

/**
 * An implementation of the gRPC {@link ServerInterceptor} for Distributed Tracing that retrieves active traces initialized by clients and lets
 * server side spans participate in the trace.
 * This implementation is based on {@linkplain https://github.com/opentracing-contrib/java-grpc/blob/master/src/main/java/io/opentracing/contrib/grpc/ServerTracingInterceptor.java}
 * 
 * @author regu.b
 *
 */
@Singleton
@Named("TracingInterceptor")
public class TracingInterceptor implements ServerInterceptor, Logging {

	/** Map of ConfigurableTracingSampler instance mapped to Service and its method*/
	private Map<String, TracingSampler> samplerMap = new HashMap<String, TracingSampler>();
	
	@Inject @Named("Tracer")
	Tracer tracer;

	public void registerTracingSamplers(List<TracingSampler> samplers, List<BindableService> services) {
		Map<Class<?>, TracingSampler> classToInstanceMap = samplers.stream()
				.collect(Collectors.toMap(Object::getClass, Function.identity()));
		services.forEach(service -> {
			AnnotationUtils.getAnnotatedMethods(service.getClass(),Traced.class).forEach(pair -> {
				Arrays.asList(pair.getValue().getAnnotation(Traced.class).withTracingSampler()).forEach(samplerClass -> {
					// Key is of the form <Service Name>+ "/" +<Method Name> 
					// reflecting the structure followed in the gRPC HandlerRegistry using MethodDescriptor#getFullMethodName()
					String samplerComponentName = (service.bindService().getServiceDescriptor().getName() + "/" 
							+ pair.getValue().getName()).toLowerCase();
					if (samplerClass == null) {
						samplerMap.put(samplerComponentName, new ConfigurableTracingSampler());
					} else {
						if (!classToInstanceMap.containsKey(samplerClass)) {
							throw new RuntimeException("TracingSampler instance not bound for TracingSampler class :" + samplerClass.getName());
						}
						samplerMap.put(samplerComponentName, classToInstanceMap.get(samplerClass));
					}
				});
			});
		});
		
	}
	
	@Override
	public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,ServerCallHandler<ReqT, RespT> next) {
		TracingSampler tracingSampler = this.samplerMap.get(call.getMethodDescriptor().getFullMethodName().toLowerCase());
		if (tracingSampler != null) {
			Map<String, String> headerMap = new HashMap<String, String>();
			for (String key : headers.keys()) {
				if (!key.endsWith(Metadata.BINARY_HEADER_SUFFIX)) {
					String value = headers.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
					headerMap.put(key, value);
				}
			}
			
			final Span span = getSpanFromHeaders(call,headerMap);
			/*
			 *  Set the client side initiated Trace and Span in the Context.
			 *  Note : we do not active the Span. This will be done in the TracingModule based on sampling enabled/not-enabled for the service's method
			 */
			Context ctxWithSpan = Context.current().withValue(OpenTracingContextKey.getKey(), span)
			        .withValue(OpenTracingContextKey.getSpanContextKey(), span.context())
			        .withValue(OpenTracingContextKey.getTracingSamplerKey(), tracingSampler); // pass on the TracingSampler for use in downstream calls for e.g. in TracingModule
			    ServerCall.Listener<ReqT> listenerWithContext = Contexts
			        .interceptCall(ctxWithSpan, call, headers, next);
			    
			return new SimpleForwardingServerCallListener<ReqT>(listenerWithContext) {};
		} else {
			return new SimpleForwardingServerCallListener<ReqT>(next.startCall(
					new SimpleForwardingServerCall<ReqT, RespT>(call){},headers)){};
		}
	}
	
	/**
	 * Creates and returns a Span from gRPC headers, if any. 
	 */
	private <ReqT, RespT> Span getSpanFromHeaders(ServerCall<ReqT, RespT> call, Map<String, String> headers) {
		String methodInvoked = call.getMethodDescriptor().getFullMethodName();
		Span span = null;
		try {
			SpanContext parentSpanCtx = tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapExtractAdapter(headers));
			span = tracer.buildSpan(methodInvoked).asChildOf(parentSpanCtx).start();
			//Service name can be added as a Tag from opentracing-java version v0.31.1 onwards
			//Tags.SERVICE.set(span, MethodDescriptor.extractFullServiceName(methodInvoked));
		} catch (IllegalArgumentException iae) {
			span = tracer.buildSpan(methodInvoked).withTag("Error", "Extract failed and an IllegalArgumentException was thrown")
					.start();
		}
		return span;
	}
}
