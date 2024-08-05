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

import com.flipkart.gjex.examples.helloworld.filter.AuthFilter;
import com.flipkart.gjex.examples.helloworld.filter.ModFilter;
import io.dropwizard.metrics5.annotation.Timed;
import com.flipkart.gjex.core.filter.grpc.ApplicationHeaders;
import com.flipkart.gjex.core.filter.grpc.MethodFilters;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.service.Api;
import com.flipkart.gjex.core.task.TaskException;
import com.flipkart.gjex.core.tracing.Traced;
import com.flipkart.gjex.examples.helloworld.bean.HelloBean;
import com.flipkart.gjex.examples.helloworld.filter.LoggingFilter;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.examples.helloworld.*;
import io.grpc.stub.StreamObserver;


/**
 * Sample Grpc service implementation that leverages GJEX features
 * @author regu.b
 *
 */
@Named("GreeterService")
public class GreeterService extends GreeterGrpc.GreeterImplBase implements Logging {

    /** Flag to return bad values in Validation check*/
    private final boolean isFailValidation = false;

    /** Property read from configuration*/
    private String greeting;

    /** Injected business logic class where validation is performed */
    private HelloBeanService helloBeanService;

    /** A stub to call an external  grpc service.This would be injected via  @{@link com.flipkart.gjex.guice.module.ClientModule}**/
    @Inject
    GreeterGrpc.GreeterBlockingStub blockingStub;

    /** Demonstrate injecting custom properties from configuration */
    @Inject
    public GreeterService(@Named("hw.greeting") String greeting, HelloBeanService helloBeanService) {
        this.greeting = greeting;
        this.helloBeanService = helloBeanService;
    }

    @Override
    @Api(deadlineConfig = "apiProperties.sayhello.deadline") // specify an API level Deadline that will cascade to all @ConcurrentTask invoked in serving this API
    @Timed // the Timed annotation for publishing JMX metrics via MBean
    @MethodFilters({LoggingFilter.class, AuthFilter.class}) // Method level filters
    @Traced(withSamplingRate=0.0f) // Start a new Trace or participate in a Client-initiated distributed trace
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {

        info("Saying hello in Greeter service");
        info("Headers in service : " + ApplicationHeaders.getHeaders());

        try {
            // invoke business logic implemented in a separate injected class
            helloBeanService.sayHelloInBean(this.getHelloBean());
        } catch (Exception exception) { // demonstrates returning any business logic or Task execution exceptions as a suitable response to the client
            this.handleException(exception, responseObserver);
            return;
        }

        // build a reply for this method invocation
        HelloReply reply = HelloReply.newBuilder().setMessage(this.greeting + req.getName()).build();

        // invoke external gRPC call
        //this.invokeGrpcCall(req, reply);

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    public boolean isFailValidation() {
        return isFailValidation;
    }

    private HelloBean getHelloBean() {
        return this.isFailValidation() ? new HelloBean() : new HelloBean("hello",10);
    }

    /** Handle exceptions in invoking delegate methods.*/
    private void handleException(Exception e, StreamObserver<HelloReply> responseObserver) {
        if (TaskException.class.isAssignableFrom(e.getClass())) {
            TaskException te = (TaskException)e;
            if (te.getCause() != null && StatusException.class.isAssignableFrom(te.getCause().getClass())) {
                responseObserver.onError((StatusException)te.getCause());
                return;
            }
        }
        responseObserver.onError(new StatusRuntimeException(Status.INTERNAL.withDescription(e.getMessage()), new Metadata()));
    }

    /** Invoke an external gRPC call as a client*/
    @SuppressWarnings("unused")
    private void invokeGrpcCall(HelloRequest req, HelloReply reply) {
        info("Saying hello to an external grpc service");
        try {
            reply = blockingStub.sayHello(req);
        }catch (Exception e){
            warn("Failed to say hello to external grpc service.Ensure Greeter service is running");
        }
    }

    @Override
    @Timed // the Timed annotation for publishing JMX metrics via MBean
    @MethodFilters({LoggingFilter.class, AuthFilter.class}) // Method level filters
    @Traced(withSamplingRate=0.0f) // Start a new Trace or participate in a Client-initiated distributed trace
    public StreamObserver<Ping> pingPong(StreamObserver<Pong> responseObserver) {

        StreamObserver<Ping> requestObserver = new StreamObserver<Ping>() {
            @Override
            public void onNext(Ping ping) {
                info("Received ping from client : " + ping.getMessage());
                Pong pong = Pong.newBuilder().setMessage("Pong").build();
                responseObserver.onNext(pong);
            }

            @Override
            public void onError(Throwable throwable) {
                error("Error in pingPong");
            }

            @Override
            public void onCompleted() {
                info("Completed pingPong");
                responseObserver.onCompleted();
            }
        };

        return requestObserver;
    }


}
