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

import com.codahale.metrics.health.HealthCheck;
import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.tracing.TracingSampler;
import com.flipkart.gjex.examples.helloworld.HelloConfiguration;
import com.flipkart.gjex.examples.helloworld.filter.AuthFilter;
import com.flipkart.gjex.examples.helloworld.filter.LoggingFilter;
import com.flipkart.gjex.examples.helloworld.healthcheck.AllIsWellHealthCheck;
import com.flipkart.gjex.examples.helloworld.service.GreeterService;
import com.flipkart.gjex.examples.helloworld.tracing.AllWhitelistTracingSampler;
import com.flipkart.gjex.grpc.channel.ChannelConfig;
import com.flipkart.gjex.guice.module.ClientModule;
import com.flipkart.gjex.guice.module.ConfigModule;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import io.grpc.BindableService;
import io.grpc.examples.helloworld.GreeterGrpc;



/**
 * Guice module for wiring sample Service to GJEX runtime
 * @author regu.b
 *
 */
public class HelloWorldModule extends AbstractModule {

	private HelloConfiguration configuration;

	public HelloWorldModule(HelloConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	protected void configure() {
		install(new ClientModule<>(GreeterGrpc.GreeterBlockingStub.class,new ChannelConfig("localhost",configuration.getGrpc().getApi().getService().getPort())));

		bind(BindableService.class).annotatedWith(Names.named("GreeterService")).to(GreeterService.class);
		bind(Filter.class).annotatedWith(Names.named("LoggingFilter")).to(LoggingFilter.class);
		bind(Filter.class).annotatedWith(Names.named("AuthFilter")).to(AuthFilter.class);
		bind(TracingSampler.class).to(AllWhitelistTracingSampler.class);
		bind(HealthCheck.class).to(AllIsWellHealthCheck.class);
	}
	
}
