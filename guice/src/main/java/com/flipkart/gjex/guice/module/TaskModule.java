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

import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.task.*;
import com.flipkart.resilience4all.resilience4j.timer.TimerConfig;
import com.flipkart.resilience4all.resilience4j.timer.TimerRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.configuration.Configuration;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * A Guice {@link AbstractModule} for managing interception of methods annotated with {@link ConcurrentTask}
 * @author regu.b
 *
 */
public class TaskModule<T> extends AbstractModule implements Logging {
	
	@Override
    protected void configure() {
		TaskMethodInterceptor methodInterceptor = new TaskMethodInterceptor();
		requestInjection(methodInterceptor);
		bindInterceptor(Matchers.any(), new TaskMethodMatcher(), methodInterceptor);
	}
	
	class TaskMethodInterceptor implements MethodInterceptor {
		
		@Inject
		@Named("GlobalFlattenedConfig")
		private Provider<Configuration> globalConfigurationProvider;

		@Inject
		private CircuitBreakerRegistry circuitBreakerRegistry;

		@Inject
		private ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry;

		@Inject
		private TimerRegistry timerRegistry;

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			ConcurrentTask task = invocation.getMethod().getAnnotation(ConcurrentTask.class);
			FutureProvider<?> futureProvider = createFutureProvider(task, invocation);
			return new FutureDecorator<>(futureProvider, task.completion());
		}

		private FutureProvider<T> createFutureProvider(ConcurrentTask task, MethodInvocation invocation) {
			Configuration globalConfig = globalConfigurationProvider.get();
			if (globalConfig.containsKey("useResilience4j") && globalConfig.getBoolean("useResilience4j")) {
				return createResilienceTaskExecutor(task, invocation);
			}
			return createHystrixTaskExecutor(task, invocation);
		}

		private ResilienceTaskExecutor<T> createResilienceTaskExecutor(ConcurrentTask task, MethodInvocation invocation) {
			String name = invocation.getMethod().getName();
			Configuration globalConfig = globalConfigurationProvider.get();
			return new ResilienceTaskExecutor<T>(
					invocation,
					circuitBreakerRegistry.circuitBreaker(name, buildCircuitBreakerConfig(task, globalConfig)),
					threadPoolBulkheadRegistry.bulkhead(name, buildThreadPoolBulkHeadConfig(task, globalConfig)),
					timerRegistry.timer(name + ".server", new TimerConfig.Builder().name(name + ".server").build()),
					task.withRequestHedging(),
					getTimeout(task, globalConfig)
			);
		}

		private ThreadPoolBulkheadConfig buildThreadPoolBulkHeadConfig(ConcurrentTask task, Configuration globalConfig) {
			int queueCapacity = globalConfig.getInt(task.resilience4jConfig()+".threadPoolBulkHead.queueCapacity");
			int maxThreadPoolSize = globalConfig.getInt(task.resilience4jConfig()+".threadPoolBulkHead.maxThreadPoolSize");
			int coreThreadPoolSize = globalConfig.getInt(task.resilience4jConfig()+".threadPoolBulkHead.coreThreadPoolSize");
			int concurrency = getConcurrency(task, globalConfig);
			if (concurrency > 0 && maxThreadPoolSize >= concurrency) {
				coreThreadPoolSize = concurrency; //Overwriting the core thread pool value if concurrency property is explicitly defined
			}
			return ThreadPoolBulkheadConfig.from(threadPoolBulkheadRegistry.getDefaultConfig())
					.queueCapacity(queueCapacity)
					.maxThreadPoolSize(maxThreadPoolSize)
					.coreThreadPoolSize(coreThreadPoolSize)
					.build();
		}

		private CircuitBreakerConfig buildCircuitBreakerConfig(ConcurrentTask task, Configuration globalConfig) {
			String slidingWindowType = globalConfig.getString(task.resilience4jConfig() + ".circuitBreaker.type");
			int slidingWindowSize = globalConfig.getInt(task.resilience4jConfig() + ".circuitBreaker.slidingWindowSize");
			return CircuitBreakerConfig.from(circuitBreakerRegistry.getDefaultConfig())
					.slidingWindowType(CircuitBreakerConfig.SlidingWindowType.valueOf(slidingWindowType))
					.slidingWindowSize(slidingWindowSize)
					.build();
		}

		private TaskExecutor<T> createHystrixTaskExecutor(ConcurrentTask task, MethodInvocation invocation) {
			Configuration configuration = globalConfigurationProvider.get();
			int timeout = getTimeout(task, configuration);
			int concurrency = getConcurrency(task, configuration);
			return new TaskExecutor<T>(invocation,
					invocation.getMethod().getDeclaringClass().getSimpleName(),
					invocation.getMethod().getName(), concurrency, timeout, task.withRequestHedging()) ; // we return the FutureDecorator and not wait for its completion. This enables responses to be composed in a reactive manner
		}
	}

	private int getTimeout(ConcurrentTask task, Configuration globalConfig) {
		int timeout = 0;
		if (task.timeoutConfig().length() > 0) { // check if timeout is specified as a config property
			timeout = globalConfig.getInt(task.timeoutConfig());
		}
		if (task.timeout() > 0) { // we take the method level annotation value as the final override
			timeout = task.timeout();
		}
		return timeout;
	}

	private int getConcurrency(ConcurrentTask task, Configuration globalConfig) {
		int concurrency = 0;
		if (task.concurrencyConfig().length() > 0) { // check if concurrency is specified as a config property
			concurrency = globalConfig.getInt(task.concurrencyConfig());
		}
		if (task.concurrency() > 0) { // we take the method level annotation value as the final override
			concurrency = task.concurrency();
		}
		return concurrency;
	}
	
	/**
	 * The Matcher that matches methods with the {@link ConcurrentTask} annotation
	 */
	class TaskMethodMatcher extends AbstractMatcher<Method> {
		@Override
	    public boolean matches(final Method method) {
	        boolean matches = false;
	        for (Annotation ann : method.getAnnotations()) {
	            final Class<? extends Annotation> annotationType = ann.annotationType();
	            if (ConcurrentTask.class.equals(annotationType)) {
	                matches = true;
	                break;
	            }
	        }
	        return matches;
	    }
	}	
}
