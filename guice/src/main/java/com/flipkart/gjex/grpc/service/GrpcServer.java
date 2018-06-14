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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flipkart.gjex.core.service.Service;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * <code>GrpcServer</code> is a {@link Service} implementation that manages the GJEX Grpc Server instance lifecycle
 * 
 * @author regunath.balasubramanian
 */

@Singleton
@Named("GrpcServer")
public class GrpcServer implements Service {

	/** Logger for this class*/
	private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServer.class);

	/** Default port number if none is specified*/
	private int port = 50051;
	
	/** The core Grpc Server instance and its builder*/
	private ServerBuilder<?> grpcServerBuilder;
	private Server grpcServer;
	
	@Inject
	public GrpcServer(@Named("Grpc.server.port") int port) {
		LOGGER.info("Creating GrpcServer listening on port : " + port);
		this.port = port;
		this.grpcServerBuilder = ServerBuilder.forPort(this.port);
	}
	
	@Override
	public void start() throws Exception {
		this.grpcServer = this.grpcServerBuilder.build().start();
		LOGGER.info("GJEX GrpcServer started.Hosting these services : ****** Start *****");
		this.grpcServer.getServices().forEach(serviceDefinition -> LOGGER.info(serviceDefinition.getServiceDescriptor().getName()));
		LOGGER.info("GJEX GrpcServer started.Hosting these services : ****** End *****");
		// Not waiting for termination as this blocks main thread preventing any subsequent startup, like the Jetty Dashboard server 
		//this.grpcServer.awaitTermination();
	}

	@Override
	public void stop() {
	    if (this.grpcServer != null) {
	    		this.grpcServer.shutdown();
	    }
		LOGGER.info("GJEX GrpcServer stopped.");
	}
	
	public void registerServices(List<BindableService> services) {
		services.forEach(service -> this.grpcServerBuilder.addService(service));
	}

}
