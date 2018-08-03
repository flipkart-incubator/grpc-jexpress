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

import javax.inject.Inject;
import javax.inject.Named;

import com.codahale.metrics.annotation.Timed;
import com.flipkart.gjex.core.filter.MethodFilters;
import com.flipkart.gjex.core.tracing.Traced;
import com.flipkart.gjex.examples.helloworld.bean.HelloBean;
import com.flipkart.gjex.examples.helloworld.filter.AuthFilter;
import com.flipkart.gjex.examples.helloworld.filter.LoggingFilter;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;

/**
 * Sample Grpc service implementation that leverages GJEX features
 * @author regu.b
 *
 */
@Named("GreeterService")
public class GreeterService extends GreeterGrpc.GreeterImplBase {

	/** Flag to return bad values in Validation check*/
	private final boolean isFailValidation = false;
	
	/** Property read from configuration*/
	private String greeting;
	
	/** Injected business logic class where validation is performed */
	private HelloBeanService helloBeanService;

	/** A stub to call an external  grpc service.This would be injected via  @{@link com.flipkart.gjex.guice.module.ClientModule}**/
	//@Inject
	//GreeterGrpc.GreeterBlockingStub blockingStub;

	/** Demonstrate injecting custom properties from configuration */
	@Inject
	public GreeterService(@Named("hw.greeting") String greeting, HelloBeanService helloBeanService) {
		this.greeting = greeting;
		this.helloBeanService = helloBeanService;
	}

	@Override
	@Timed // the Timed annotation for publishing JMX metrics via MBean
	@MethodFilters({LoggingFilter.class, AuthFilter.class}) // Method level filters
	@Traced // Start a new Trace or participate in a Client-initiated distributed trace
	public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
		
		// invoke business logic implemented in a separate injected class
		helloBeanService.sayHelloInBean(this.getHelloBean());
		
		// build a reply for this method invocation
		HelloReply reply = HelloReply.newBuilder().setMessage(this.greeting + req.getName()).build();

		/*
		System.out.println("Saying hello to an external grpc service");
		try {
			reply = blockingStub.sayHello(req);
		}catch (Exception e){
			System.out.println("Failed to say hello to external grpc service.Ensure Greeter service is running");
		}
		*/

		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}

	public boolean isFailValidation() {
		return isFailValidation;
	}

	private HelloBean getHelloBean() {
		return this.isFailValidation() ? new HelloBean() : new HelloBean("hello",10);
	}
	
}

