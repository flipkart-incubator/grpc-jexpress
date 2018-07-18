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
package com.flipkart.gjex.grpc.service;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.service.AbstractService;
import com.flipkart.gjex.core.service.Service;
import com.flipkart.gjex.grpc.interceptor.FilterInterceptor;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

/**
 * <code>GrpcServer</code> is a {@link Service} implementation that manages the GJEX Grpc Server instance lifecycle
 * 
 * @author regunath.balasubramanian
 */

@Singleton
@Named("GrpcServer")
public class GrpcServer extends AbstractService implements Logging {

	/** Default port number if none is specified*/
	private int port = 50051;
	
	/** The core Grpc Server instance and its builder*/
	private ServerBuilder<?> grpcServerBuilder;
	private Server grpcServer;
	
	/** The Filter ServerInterceptor*/
	private FilterInterceptor filterInterceptor;
	
	@Inject
	public GrpcServer(@Named("Grpc.server.port") int port, @Named("FilterInterceptor") FilterInterceptor filterInterceptor) {
		info("Creating GrpcServer listening on port : " + port);
		this.port = port;
		this.grpcServerBuilder = ServerBuilder.forPort(this.port);
		this.filterInterceptor = filterInterceptor;
	}
	
	@Override
	public void doStart() throws Exception {
		this.grpcServer = this.grpcServerBuilder.build().start();
		info("GJEX GrpcServer started.Hosting these services : ****** Start *****");
		this.grpcServer.getServices().forEach(serviceDefinition -> info(serviceDefinition.getServiceDescriptor().getName()));
		info("GJEX GrpcServer started.Hosting these services : ****** End *****");
		// Not waiting for termination as this blocks main thread preventing any subsequent startup, like the Jetty Dashboard server 
		// this.grpcServer.awaitTermination();
	}

	@Override
	public void doStop() {
	    if (this.grpcServer != null) {
	    		this.grpcServer.shutdown();
	    }
		info("GJEX GrpcServer stopped.");
	}

	public void registerFilters(@SuppressWarnings("rawtypes") List<Filter> filters, List<BindableService> services) {
		this.filterInterceptor.registerFilters(filters, services);
	}

	public void registerServices(List<BindableService> services) {
		services.forEach(service -> this.grpcServerBuilder.addService(ServerInterceptors.intercept (service, this.filterInterceptor)));
	}

}
