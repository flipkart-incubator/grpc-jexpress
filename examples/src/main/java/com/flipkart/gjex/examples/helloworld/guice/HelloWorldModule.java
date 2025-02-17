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
package com.flipkart.gjex.examples.helloworld.guice;

import com.flipkart.gjex.core.filter.grpc.GrpcFilter;
import com.flipkart.gjex.core.filter.http.HttpFilterParams;
import com.flipkart.gjex.core.filter.http.JavaxFilterParams;
import com.flipkart.gjex.core.tracing.TracingSampler;
import com.flipkart.gjex.examples.helloworld.filter.AuthFilter;
import com.flipkart.gjex.examples.helloworld.filter.CustomHeaderHttpFilter;
import com.flipkart.gjex.examples.helloworld.filter.LoggingFilter;
import com.flipkart.gjex.examples.helloworld.service.GreeterService;
import com.flipkart.gjex.examples.helloworld.tracing.AllWhitelistTracingSampler;
import com.flipkart.gjex.examples.helloworld.web.HelloWorldResourceConfig;
import com.flipkart.gjex.examples.helloworld.web.javaxfilter.ExampleJavaxFilter;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.grpc.BindableService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.helloworld.GreeterGrpc;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Guice module for wiring sample Service to GJEX runtime
 * @author regu.b
 *
 */
public class HelloWorldModule extends AbstractModule {

	public HelloWorldModule() {}

	@Override
	protected void configure() {
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50051)
				.usePlaintext()
				.build();
//		install(new ClientModule<GreeterGrpc.GreeterBlockingStub>(GreeterGrpc.GreeterBlockingStub.class,new ChannelConfig("localhost",9999)));
		bind(GreeterGrpc.GreeterBlockingStub.class).toInstance(GreeterGrpc.newBlockingStub(channel));
		bind(BindableService.class).annotatedWith(Names.named("GreeterService")).to(GreeterService.class);
		bind(GrpcFilter.class).annotatedWith(Names.named("LoggingFilter")).to(LoggingFilter.class);
		bind(GrpcFilter.class).annotatedWith(Names.named("AuthFilter")).to(AuthFilter.class);
		bind(TracingSampler.class).to(AllWhitelistTracingSampler.class);
		bind(ResourceConfig.class).annotatedWith(Names.named("HelloWorldResourceConfig")).to(HelloWorldResourceConfig.class);
        bind(JavaxFilterParams.class).annotatedWith(Names.named("ExampleJavaxFilter")).toInstance(JavaxFilterParams.builder().filter(new ExampleJavaxFilter()).pathSpec("/*").build());
        bind(HttpFilterParams.class).annotatedWith(Names.named("CustomHeaderHttpFilter")).toInstance(HttpFilterParams.builder().filter(new CustomHeaderHttpFilter()).pathSpec("/*").build());
	}
}
