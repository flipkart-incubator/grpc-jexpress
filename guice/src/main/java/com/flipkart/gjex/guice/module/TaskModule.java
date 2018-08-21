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

import javax.inject.Inject;
import javax.inject.Named;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.configuration.Configuration;

import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.task.ConcurrentTask;
import com.flipkart.gjex.core.task.FutureDecorator;
import com.flipkart.gjex.core.task.TaskExecutor;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;

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
		
		@Inject @Named("GlobalConfig")
		Configuration globalConfig;
		
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			ConcurrentTask task = invocation.getMethod().getAnnotation(ConcurrentTask.class);
			int timeout = 0;
			if (task.timeoutConfig().length() > 0) { // check if timeout is specified as a config property
				timeout = globalConfig.getInt(task.timeoutConfig());
			}
			if (task.timeout() > 0) { // we take the method level annotation value as the final override
				timeout = task.timeout();
			}
			int concurrency = 0;
			if (task.concurrencyConfig().length() > 0) { // check if concurrency is specified as a config property
				concurrency = globalConfig.getInt(task.concurrencyConfig());
			}
			if (task.concurrency() > 0) { // we take the method level annotation value as the final override
				concurrency = task.concurrency();
			}
			return new FutureDecorator<T>(new TaskExecutor<T>(invocation,
					invocation.getMethod().getDeclaringClass().getSimpleName(),
					invocation.getMethod().getName(), concurrency, timeout),task.completion()) ; // we return the FutureDecorator and not wait for its completion. This enables responses to be composed in a reactive manner
		}
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
