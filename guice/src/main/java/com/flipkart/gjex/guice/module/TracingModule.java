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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.tracing.OpenTracingContextKey;
import com.flipkart.gjex.core.tracing.Traced;
import com.flipkart.gjex.core.tracing.TracingSampler;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import io.grpc.Context;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * An implementation of Guice {@link AbstractModule} that initializes OpenTracing and intercepts methods annotated with {@link Traced}
 * annotation
 * 
 * @author regu.b
 */
public class TracingModule extends AbstractModule implements Logging {
	
	@Override
    protected void configure() {
		TracedMethodInterceptor methodInterceptor = new TracedMethodInterceptor();
		requestInjection(methodInterceptor);
		bindInterceptor(Matchers.any(), new TracedMethodMatcher(), methodInterceptor);
	}
	
	/**
	 * Creates an OpenTracing Tracer over the Openzipkin-Brave tracer
	 */
	@Named("Tracer")
	@Provides
	@Singleton
	Tracer getTracer(@Named("Tracing.collector.endpoint")String endpoint) {
		AsyncReporter<Span> spanReporter = AsyncReporter.create(OkHttpSender.create(endpoint));
		Tracing tracing = Tracing.newBuilder()
                 .localServiceName("GJEX")
                 .spanReporter(spanReporter)
                 .build();
		 return BraveTracer.create(tracing);
	}
	
	/**
	 * The Tracing method interceptor
	 */
	class TracedMethodInterceptor implements MethodInterceptor {
		
		/** The OpenTracing Tracer instance*/
		@Inject @Named("Tracer")
		Tracer tracer;
		
		/**
		 * Starts a Trace(implicitly) or adds a Span for every method annotated with {@link Traced}. Nesting of spans is implicit
		 */
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			Scope scope = null;
			/*
			 * Initializing method invocation span as null means the current active span may get unset if there is no parent active span or the Tracing sampler returns 
			 * negative for sampling the request
			 */
			io.opentracing.Span methodInvocationSpan = null;  
			Callable<Object> methodCallable = new MethodCallable(invocation);
			if (OpenTracingContextKey.activeSpan() != null) {
				String methodInvoked = (invocation.getMethod().getDeclaringClass().getSimpleName() + "." + invocation.getMethod().getName()).toLowerCase();
				TracingSampler tracingSampler = OpenTracingContextKey.activeTracingSampler();
				tracingSampler.initializeSamplerFor(methodInvoked, invocation.getMethod().getAnnotation(Traced.class).withSamplingRate());
				if (tracingSampler.isSampled(methodInvoked)) {
					methodInvocationSpan = tracer.buildSpan(methodInvoked)
							.asChildOf(OpenTracingContextKey.activeSpan())
							.start();
					scope = tracer.scopeManager().activate(methodInvocationSpan, true);					
				}
				// Set the Method invocation Span as the current span - may be null too and this means subsequent methods will not get traced
				methodCallable = Context.current().withValue(OpenTracingContextKey.getKey(), methodInvocationSpan).wrap(methodCallable);
			}
			Object result = null;
			try  {
				result = methodCallable.call();
			} catch(Exception ex) {
				if (methodInvocationSpan != null) {
				    Tags.ERROR.set(methodInvocationSpan, true);
				    methodInvocationSpan.log(ImmutableMap.of(Fields.EVENT, "error", Fields.ERROR_OBJECT, ex, Fields.MESSAGE, ex.getMessage()));
				}
			} finally {
				if (scope != null) {
					scope.close();
				}
			}
			return result;
		}
	}	
	
	/**
	 * The Matcher that matches methods with the {@link Traced} annotation
	 */
	class TracedMethodMatcher extends AbstractMatcher<Method> {
		@Override
	    public boolean matches(final Method method) {
	        boolean matches = false;
	        for (Annotation ann : method.getAnnotations()) {
	            final Class<? extends Annotation> annotationType = ann.annotationType();
	            if (Traced.class.equals(annotationType)) {
	                matches = true;
	                break;
	            }
	        }
	        return matches;
	    }
	}
	
	/** Wraps a MethodInvocation as a Callable for use with gRPC Context*/
	class MethodCallable implements Callable<Object> {
		MethodInvocation invocation ;
		MethodCallable(MethodInvocation invocation) {
			this.invocation = invocation;
		}
		public Object call() throws Exception {
			try {
				return this.invocation.proceed();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}
}
