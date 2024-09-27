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
package com.flipkart.gjex.examples.helloworld.service;

import java.util.concurrent.Future;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.flipkart.gjex.core.filter.grpc.ApplicationHeaders;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.task.AsyncResult;
import com.flipkart.gjex.core.task.ConcurrentTask;
import com.flipkart.gjex.core.task.FutureDecorator;
import com.flipkart.gjex.core.tracing.Traced;
import com.flipkart.gjex.examples.helloworld.bean.HelloBean;

import io.reactivex.functions.BiFunction;

/**
 * A sample business logic implementation that is called with an entity that is validated for correctness
 *
 * @author regu.b
 *
 */
public class HelloBeanService implements Logging {

	/** Method params with javax.validation annotations to force validation*/
	@Traced
	public void sayHelloInBean(@NotNull @Valid HelloBean helloBean) {
		info("A valid HelloBean has been passed.");
		addToTrace("Request", helloBean.toString());
		this.tracedMethod1();
	}

	// ------- Methods to demonstrate Tracing flow and async execution

	/** Traced method with serial invocation of next method*/
	@Traced
	public void tracedMethod1() {
		info("Invoked trace method1");
//        this.tracedMethod2();
//        this.tracedMethod3();
	}

	/**
	 * Traced method with following
	 * 1. Invoke two methods concurrently
	 * 2. Compose response
	 * 3. Invoke next method serially
	 */
	@Traced
	public void tracedMethod2() {
		info("Invoked trace method2");
		ResponseEntity entity = new ResponseEntity();
		FutureDecorator<ResponseEntity> future1 = (FutureDecorator<ResponseEntity>)this.tracedMethod3(entity);
		FutureDecorator<ResponseEntity> future2 = (FutureDecorator<ResponseEntity>)this.tracedMethod4(entity);
		ResponseEntity finalEntity = FutureDecorator.compose(future1, future2,
				new BiFunction<ResponseEntity,ResponseEntity,ResponseEntity>() {
					@Override
					public ResponseEntity apply(ResponseEntity t1, ResponseEntity t2) throws Exception {
						ResponseEntity finalEntity = new ResponseEntity();
						finalEntity.method2 = entity.method2;
						if (t1 != null) { // call to get t1 may have timedout or errored. Execution was allowed to proceed as it is optional
							finalEntity.method3 = entity.method3; // we use the value only if the call was successful, else it is null
						}
						finalEntity.method4 = t2.method4;
						return finalEntity;
					}
		});
		this.tracedMethod5(finalEntity);
		info("Final response : " + finalEntity.method5);
	}

	/**
	 * A Concurrent Traced task executing in its own threadpool via HystrixCommand. Modifies the entity passed to it
	 * Here timeout is configured as an explicit value. This method is also marked as {@link ConcurrentTask.Completion#Optional}
	 */
	@Traced
	@ConcurrentTask(completion=ConcurrentTask.Completion.Optional, timeout = 300, withRequestHedging = true)
	// Task is marked to timeout, execution will proceed as this is marked as Optional, Request hedging is enabled for this Task
	public Future<ResponseEntity> tracedMethod3(ResponseEntity entity) {
		return new AsyncResult<ResponseEntity>() {
            @Override
            public ResponseEntity invoke() {
            		sleep(200);
            		info("Headers in method3 : " + ApplicationHeaders.getHeaders());
            		info("Invoked trace method3");
            		entity.method3 = "InvokedMethod3";
            		return entity;
            }
        };
	}

	/**
	 * A Concurrent Traced task executing in its own threadpool via HystrixCommand. Modifies the entity passed to it
	 * Timeout is configured as a config property in application configuration i.e. hello_world_config.yml in this example
	 * This method is implicitly(default) marked as {@link ConcurrentTask.Completion#Mandatory}
	 */
	@Traced
	@ConcurrentTask(timeoutConfig = "taskProperties.hello.timeout")
	public Future<ResponseEntity> tracedMethod4(ResponseEntity entity) {
		return new AsyncResult<ResponseEntity>() {
            @Override
            public ResponseEntity invoke() {
            		sleep(100);
            		info("Invoked trace method4");
            		entity.method4 = "InvokedMethod4";
            		return entity;
            }
        };
	}

	/** Traced method invoking the next method serially*/
	@Traced
	public void tracedMethod5(ResponseEntity entity) {
		info("Invoked trace method5");
		entity.method5 = entity.method3 + "-" + entity.method4;
		this.tracedMethod6();
	}

	/** Traced method that doesnot invoke anything else*/
	@Traced
	public void tracedMethod6() {
		info("Invoked trace method6");
	}

	/** Collector/Entity to collect/hold data from multiple concurrent task executions*/
	class ResponseEntity {
		String method2;
		String method3;
		String method4;
		String method5;
	}

	/** Puts the current Thread to sleep*/
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// do nothing
		}
	}
}
