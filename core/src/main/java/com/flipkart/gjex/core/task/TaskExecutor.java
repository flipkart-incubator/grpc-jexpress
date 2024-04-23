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
package com.flipkart.gjex.core.task;

import org.aopalliance.intercept.MethodInvocation;

import com.flipkart.gjex.core.logging.Logging;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

import io.grpc.Context;
import io.reactivex.functions.BiConsumer;

import java.util.concurrent.Future;

/**
 * A {@link HystrixCommand} implementation to provide async execution and circuit breaking functionality for method invocations.
 * @author regu.b
 *
 */
public class TaskExecutor<T> extends HystrixCommand<T> implements FutureProvider<T>, Logging {

	/** The MethodInvocation to execute asynchronously*/
	private final MethodInvocation invocation;
	
	/** The currently active gRPC Context*/
	private Context currentContext;
	
	/** The completion BiConsumer*/
	private BiConsumer<T, Throwable> completionConsumer;
	
	/** Indicates if requests may be hedged within the configured timeout duration*/
	private boolean withRequestHedging;
	
	/** The rolling tail latency as seen by Hystrix*/
	private long rollingTailLatency;
	
	/** Attributes stored for cloning*/
	private String groupKey;
	private String name;
	private int concurrency;
	private int timeout;
		
	public TaskExecutor(MethodInvocation invocation, String groupKey, String name, int concurrency, int timeout, boolean withRequestHedging) {
		super(Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
		        .andCommandKey(HystrixCommandKey.Factory.asKey(name))
		        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(name + "-tp")) // creating a new thread pool per task by appending "-tp" to the task name
		        .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(concurrency))
		        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
	                .withExecutionIsolationStrategy(ExecutionIsolationStrategy.THREAD)
	                .withExecutionTimeoutInMilliseconds(timeout)));
		currentContext = Context.current(); // store the current gRPC Context
		this.invocation = invocation;
		this.timeout = timeout;
		this.withRequestHedging = withRequestHedging;
		this.rollingTailLatency = HystrixCommandMetrics.getInstance(HystrixCommandKey.Factory.asKey(name)).getTotalTimePercentile(95);
		
		// attributes copied for cloning, if needed
		this.groupKey = groupKey;
		this.name = name;
		this.concurrency = concurrency;
	}

	public void setCompletionConsumer(BiConsumer<T, Throwable> completionConsumer) {
		this.completionConsumer = completionConsumer;
	}
	public MethodInvocation getInvocation() {
		return invocation;
	}
	public boolean isWithRequestHedging() {
		return withRequestHedging;
	}
	public long getRollingTailLatency() {
		return rollingTailLatency;
	}

	@Override
	public String getName() {
		return getInvocation().getMethod().getName();
	}

	public int getTimeout() {
		return timeout;
	}

	@Override
	public Future<T> getFuture() {
		return queue();
	}

	/**
	 * Clone this TaskExecutor with values that were specified during instantiation
	 */
	public TaskExecutor<T> clone() {
		TaskExecutor<T> clone = new TaskExecutor<T>(this.invocation, this.groupKey, this.name, this.concurrency, this.timeout, this.withRequestHedging);
		return clone;
	}

	/**
	 * Overridden method implementation. Invokes the Method invocation while setting relevant current gRPC Context
	 * @see com.netflix.hystrix.HystrixCommand#run()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected T run() throws Exception {
		Context previous = this.currentContext.attach(); // setting the current gRPC context for the executing Hystrix thread
		Throwable error = null;
		T result = null;
		try {
			result = ((AsyncResult<T>)this.invocation.proceed()).invoke(); // call the AsyncResult#invoke() to execute the actual work to be performed asynchronously
			return result;
		} catch (Throwable e) {
			error = e;
			error("Error executing task", e);
			throw new RuntimeException(e);
		} finally {
			if (this.completionConsumer != null) {
				this.completionConsumer.accept(result, error); // inform the completion status to the registered completion consumer
			}
			this.currentContext.detach(previous); // unset the current gRPC context
		}
	}
	
}
