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

import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.context.GJEXContext;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.task.TaskException;
import com.flipkart.gjex.core.tracing.Traced;
import com.flipkart.gjex.core.tracing.TracingSampler;
import com.flipkart.gjex.core.tracing.TracingSamplerHolder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import io.grpc.BindableService;
import io.grpc.Context;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;


/**
 * An implementation of Guice {@link AbstractModule} that initializes OpenTracing and intercepts methods annotated with {@link Traced}
 * annotation
 *
 * @author regu.b
 */
public class TracingModule<T> extends AbstractModule implements Logging {

    @Override
    protected void configure() {
        TracedMethodInterceptor methodInterceptor = new TracedMethodInterceptor();
        requestInjection(methodInterceptor);
        bindInterceptor(Matchers.any(), new TracedMethodMatcher(), methodInterceptor);
        bind(TracingSamplerHolder.class).annotatedWith(Names.named("TracingSamplerHolder")).to(TracingSamplerHolder.class);
    }

    @Named("Tracer")
    @Provides
    @Singleton
    Tracer getTracer(GJEXConfiguration configuration) {
        String endPoint = configuration.getTracing().getCollectorEndpoint();

        // Create an OTLP exporter
        OtlpGrpcSpanExporter exporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(endPoint)
                .build();

        // Create a BatchSpanProcessor and add the exporter
        BatchSpanProcessor spanProcessor = BatchSpanProcessor.builder(exporter).build();

        // Build the OpenTelemetry SDK with the BatchSpanProcessor
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(spanProcessor)
                .build();

        // Initialize the OpenTelemetry SDK
        OpenTelemetrySdk openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal();

        // Get the Tracer
        return openTelemetry.getTracer("GJEX");
    }

    /**
     * Helper to log error to the traced Span
     */
    private void logErrorToSpan(Span methodInvocationSpan, Exception ex) {
        if (methodInvocationSpan != null) {
            methodInvocationSpan.recordException(ex);
            methodInvocationSpan.setStatus(StatusCode.ERROR, ex.getMessage());
        }
    }

    /**
     * The Tracing method interceptor
     */
    class TracedMethodInterceptor implements MethodInterceptor {

        /**
         * The OpenTracing Tracer instance
         */
        @Inject
        @Named("Tracer")
        private Provider<Tracer> tracerProvider;

        /**
         * Starts a Trace(implicitly) or adds a Span for every method annotated with {@link Traced}. Nesting of spans is implicit
         */
        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public Object invoke(MethodInvocation invocation) {
            /*
             * Initializing method invocation span as null means the current active span may get unset if there is no parent active span or the Tracing sampler returns
             * negative for sampling the request
             */
            Span methodInvocationSpan = null;
            Callable<Object> methodCallable = new MethodCallable(invocation);
            if (GJEXContext.activeSpan() != null) {
                String methodInvoked = (invocation.getMethod().getDeclaringClass().getSimpleName() + "." + invocation.getMethod().getName()).toLowerCase();
                // check and warn if TracingSampler is used for non BindableService classes
                if (!BindableService.class.isAssignableFrom(invocation.getMethod().getDeclaringClass()) && invocation.getMethod().getAnnotation(Traced.class).withTracingSampler() != TracingSampler.class) {
                    warn("TracingSampler declarations are interpreted only for sub-types of gRPC BindableService. TracingSampler declared for : "
                            + methodInvoked + " will not be interpreted/honored");
                }
                TracingSampler tracingSampler = GJEXContext.activeTracingSampler();
                tracingSampler.initializeSamplerFor(methodInvoked, invocation.getMethod().getAnnotation(Traced.class).withSamplingRate());
                Tracer tracer = tracerProvider.get();
                if (tracingSampler.isSampled(methodInvoked)) {
                    // Get the current active span
                    Span currentSpan = Span.current();

                    // Start a new span as a child of the current span
                    methodInvocationSpan = tracer.spanBuilder(methodInvoked)
                            .setParent(io.opentelemetry.context.Context.current().with(currentSpan))
                            .startSpan();
                    // Make the new span the current span
                    try (Scope scope = methodInvocationSpan.makeCurrent()) {
                        // The new span is now set as the current span in this scope.
                        // It will be reset to the previous span when the scope is closed.
                    }
                }
                // Set the Method invocation Span as the current span - may be null too and this means subsequent methods will not get traced
                methodCallable = Context.current().withValue(GJEXContext.getKeyActiveSpan(), methodInvocationSpan).wrap(methodCallable);
            }
            Object result;
            try {
                result = methodCallable.call();
            } catch (Exception ex) { // we want to log errors to the trace only once
                TaskException tex;
                if (TaskException.class.isAssignableFrom(ex.getClass())) {
                    tex = (TaskException) ex;
                    if (tex.isTraced()) {
                        logErrorToSpan(methodInvocationSpan, ex);
                        tex.setTraced(false);
                    }
                } else {
                    logErrorToSpan(methodInvocationSpan, ex);
                    tex = new TaskException(ex, false);
                }
                throw tex;
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

    /**
     * Wraps a MethodInvocation as a Callable for use with gRPC Context
     */
    class MethodCallable implements Callable<Object> {
        MethodInvocation invocation;

        MethodCallable(MethodInvocation invocation) {
            this.invocation = invocation;
        }

        public Object call() throws Exception {
            try {
                return this.invocation.proceed();
            } catch (Throwable e) {
                if (Exception.class.isAssignableFrom(e.getClass())) {
                    throw (Exception) e;
                }
                throw new RuntimeException(e);
            }
        }
    }
}
