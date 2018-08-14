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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.task.ConcurrentTask;
import com.flipkart.gjex.core.tracing.Traced;
import com.flipkart.gjex.examples.helloworld.bean.HelloBean;

/**
 * A sample business logic implementation that is called with an entity that is validated for correctness
 * 
 * @author regu.b
 *
 */
public class HelloBeanService implements Logging {
	
	/** Method params with javax.validation annotations to force validation*/
	@Traced
	@ConcurrentTask(timeout = 100)
	public void sayHelloInBean(@NotNull @Valid HelloBean helloBean) {
		info("A valid HelloBean has been passed.");
		addToTrace("Request", helloBean.toString());
		this.tracedMethod1();
	}
	
	/** Synthetic methods to demonstrate Tracing flow*/
	@Traced
	@ConcurrentTask(timeout = 100)
	public void tracedMethod1() {
		info("Invoked trace method1");
		this.tracedMethod2();
	}
	@Traced
	@ConcurrentTask(timeout = 100)
	public void tracedMethod2() {
		info("Invoked trace method2");
		this.tracedMethod3();
	}
	@Traced
	@ConcurrentTask(timeout = 100)
	public void tracedMethod3() {
		info("Invoked trace method3");
		this.tracedMethod4();
	}
	@Traced
	@ConcurrentTask(timeout = 100)
	public void tracedMethod4() {
		info("Invoked trace method4");
	}
	
}
