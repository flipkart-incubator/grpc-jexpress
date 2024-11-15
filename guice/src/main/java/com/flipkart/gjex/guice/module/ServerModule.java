/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.flipkart.gjex.guice.module;

import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.healthcheck.GrpcHealthCheckService;
import com.flipkart.gjex.core.service.Service;
import com.flipkart.gjex.core.setup.Bootstrap;
import com.flipkart.gjex.grpc.interceptor.FilterInterceptor;
import com.flipkart.gjex.grpc.interceptor.StatusMetricInterceptor;
import com.flipkart.gjex.grpc.interceptor.TracingInterceptor;
import com.flipkart.gjex.grpc.service.ApiServer;
import com.flipkart.gjex.grpc.service.DashboardServer;
import com.flipkart.gjex.grpc.service.GrpcServer;
import com.flipkart.gjex.grpc.service.ScheduledJobManager;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.grpc.health.v1.HealthGrpc;

import java.util.Map;

/**
 * <code>ServerModule</code> is a Guice {@link AbstractModule} implementation used for configuring the Grpc Server and Dashboard server.
 *
 * @author regunath.balasubramanian
 *
 */
public class ServerModule<T extends GJEXConfiguration, U extends Map> extends AbstractModule {

  private final Bootstrap<T,U> bootstrap;

  public ServerModule(Bootstrap<T,U> bootstrap) {
    this.bootstrap = bootstrap;
  }

	@Override
    protected void configure() {
		bind(FilterInterceptor.class).annotatedWith(Names.named("FilterInterceptor")).to(FilterInterceptor.class);
		bind(TracingInterceptor.class).annotatedWith(Names.named("TracingInterceptor")).to(TracingInterceptor.class);
		bind(StatusMetricInterceptor.class).annotatedWith(Names.named("StatusMetricInterceptor")).to(StatusMetricInterceptor.class);
		bind(Service.class).annotatedWith(Names.named("GrpcServer")).to(GrpcServer.class);
		bind(HealthGrpc.HealthImplBase.class).annotatedWith(Names.named("GrpcHealthCheckService"))
      .toInstance(new GrpcHealthCheckService(bootstrap));
		bind(Service.class).annotatedWith(Names.named("DashboardServer")).to(DashboardServer.class);
		bind(Service.class).annotatedWith(Names.named("APIServer")).to(ApiServer.class);
		bind(Service.class).annotatedWith(Names.named("ScheduleJobManager")).to(ScheduledJobManager.class);
	}
}
