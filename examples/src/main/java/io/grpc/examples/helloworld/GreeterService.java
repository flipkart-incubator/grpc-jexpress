/*
 * Copyright 2012-2016, the original author or authors.
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
package io.grpc.examples.helloworld;

import javax.inject.Inject;
import javax.inject.Named;

import com.yammer.metrics.annotation.Timed;

import io.grpc.stub.StreamObserver;

/**
 * Sample Grpc service implementation that leverages GJEX features
 * @author regu.b
 *
 */
@Named("GreeterService")
public class GreeterService extends GreeterGrpc.GreeterImplBase {
	
	private String greeting;
	
	// demonstrate injecting custom properties from configuration
	@Inject
	public GreeterService(@Named("hw.greeting") String greeting) {
		this.greeting = greeting;
	}
	
    @Override
    @Timed // the Timed annotation for publishing JMX metrics via MBean
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
      HelloReply reply = HelloReply.newBuilder().setMessage(this.greeting + req.getName()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }	

