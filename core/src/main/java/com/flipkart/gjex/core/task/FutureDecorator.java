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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.Context;
import io.grpc.Status;
import io.grpc.StatusException;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function3;

/**
 * A Decorated {@link Future} that delegates all calls to an origin {@link Future}
 * Provides methods to compose responses from realization of instances of this Future sub-type, evaluating completion as specified by {@link ConcurrentTask#completion()}
 * 
 * @author regu.b
 *
 */
public class FutureDecorator<T> implements Future<T> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FutureDecorator.class);

	/** The Completion indicator*/
	private final ConcurrentTask.Completion completion;
	/** The original Future decorated by this Future*/
	private final Future<T> origin;
	/** The TaskExecutor producing the Future*/
	private final TaskExecutor<T> taskExecutor;
	
	/** The BiConsumer to callback on completion*/
	private BiConsumer<T, Throwable> completionConsumer;
	
	public FutureDecorator(TaskExecutor<T> taskExecutor, ConcurrentTask.Completion completion) {
		this.taskExecutor = taskExecutor;
		this.origin = taskExecutor.queue();
		this.completion = completion;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return origin.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return origin.isCancelled();
	}

	@Override
	public boolean isDone() {
		return origin.isDone();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get() throws InterruptedException, ExecutionException {
		T result = origin.get();
		if (result instanceof Future) {
			return ((Future<T>) result).get();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		T result = origin.get(timeout, unit);
		if (result instanceof Future) {
			return ((Future<T>) result).get();
		}
		return result;
	}

	public ConcurrentTask.Completion getCompletion() {
		return completion;
	}
	public TaskExecutor<T> getTaskExecutor() {
		return taskExecutor;
	}

	/**
	 * Registers the specified BiConsumer for callback when this FutureDecorator completes
	 * @param action the callback BiConsumer action
	 */
	public void whenComplete(BiConsumer<T,Throwable> action) {
		this.completionConsumer = action;
		this.taskExecutor.setCompletionConsumer(this.completionConsumer);
	}
	
	/** Compose response from evaluating results from the specified 2 FutureDecorator instances*/
	@SuppressWarnings("unchecked")
	public static <T1,T2,R> R compose (FutureDecorator<? extends T1>future1, 
			FutureDecorator<? extends T2> future2, BiFunction<? super T1, ? super T2, ? extends R> composer) throws TaskException {
		R r = null;
		T1 t1 = (T1)getResultFromFuture(future1);
		T2 t2 = (T2)getResultFromFuture(future2);
		try {
			r = composer.apply(t1, t2);
		} catch (Exception e) {
			LOGGER.error("Error composing result from Futures : " + e.getMessage(), e);
			throw new TaskException("Error composing result from Futures : " + e.getMessage(), e);
		}
		return r;
	}

	/** Compose response from evaluating results from the specified 3 FutureDecorator instances*/
	@SuppressWarnings("unchecked")
	public static <T1,T2,T3,R> R compose (FutureDecorator<? extends T1>future1, 
			FutureDecorator<? extends T2> future2, FutureDecorator<? extends T3> future3,
			Function3<? super T1, ? super T2, ? super T3, ? extends R> composer) throws TaskException {
		R r = null;
		T1 t1 = (T1)getResultFromFuture(future1);
		T2 t2 = (T2)getResultFromFuture(future2);
		T3 t3 = (T3)getResultFromFuture(future3);
		try {
			r = composer.apply(t1, t2, t3);
		} catch (Exception e) {
			LOGGER.error("Error composing result from Futures : " + e.getMessage(), e);
			throw new TaskException("Error composing result from Futures : " + e.getMessage(), e);
		}
		return r;
	}
	
	/** Convenience method to get the response from completion of the specified FutureDecorator and evaluate completion as defined in the FutureDecorator*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object getResultFromFuture(FutureDecorator future) {
		Object result = null;
		Integer futureGetTimeout = null;
		// check if a Deadline has been set. This will become the first timeout we consider
		if (Context.current().getDeadline() != null) {
			if (Context.current().getDeadline().isExpired()) {
				LOGGER.error("Task execution evaluation failed.Deadline exceeded in server execution.");
				throw new TaskException("Task execution evaluation failed.", 
						new StatusException(Status.DEADLINE_EXCEEDED.withDescription("Deadline exceeded in server execution.")));
			}
			futureGetTimeout = (int)Context.current().getDeadline().timeRemaining(TimeUnit.MILLISECONDS);
		}
		// see if a Task timeout has been set, use the smaller of the Deadline and Task timeout
		if (future.getTaskExecutor().getTimeout() > 0) {
			futureGetTimeout = Math.min(futureGetTimeout, future.getTaskExecutor().getTimeout());
		}
		if (future.getTaskExecutor().isWithRequestHedging() && future.getTaskExecutor().getRollingTailLatency() > 0) {
			// we'll take the minimum of deadline and rolling tail latency (if request hedging is enabled) as the timeout for the Future
			futureGetTimeout = futureGetTimeout == null ? 
					(int)future.getTaskExecutor().getRollingTailLatency() 
					: Math.min(futureGetTimeout, (int)future.getTaskExecutor().getRollingTailLatency());
		}
		try {
			if (futureGetTimeout != null) {
				result = future.get(futureGetTimeout.longValue(), TimeUnit.MILLISECONDS);
			} else {
				result = future.get();
			}
		} catch (TimeoutException e) {
			if (future.getTaskExecutor().isWithRequestHedging() && !Context.current().getDeadline().isExpired()) {
				// we will reschedule the execution i.e. hedge the request and return the result
				LOGGER.info("Sending hedged request for Task : " + future.getTaskExecutor().getInvocation().getMethod().getName());
				result = FutureDecorator.getResultFromFuture(new FutureDecorator(future.getTaskExecutor().clone(), future.getCompletion()));
			}
		} catch (InterruptedException | ExecutionException e) {
			String errorMessage =  e.getCause() == null ? e.getMessage() :  e.getCause().getMessage();
			if (future.getCompletion().equals(ConcurrentTask.Completion.Mandatory)) {
				if (TimeoutException.class.isAssignableFrom(e.getClass())) {
					throw new TaskException("Task execution results not available.", 
							new StatusException(Status.DEADLINE_EXCEEDED.withDescription("Deadline exceeded waiting for results :" + e.getMessage())));
				}
				throw new TaskException("Error executing mandatory Task : " + errorMessage, e);
			} else {
				LOGGER.warn("Execution exception in optional task :" + errorMessage + " . Not failing the execution and proceeding.");
			}
		}
		return result;
	}
	
}