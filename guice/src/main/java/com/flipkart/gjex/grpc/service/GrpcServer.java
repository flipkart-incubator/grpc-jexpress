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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flipkart.gjex.core.service.Service;

import io.grpc.Server;
import io.grpc.ServerBuilder;

@Singleton
public class GrpcServer implements Service {

	/** Logger for this class*/
	private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServer.class);

	private int port = 50051;
	private ServerBuilder grpcServerBuilder;
	private Server grpcServer;
	
	@Inject
	public GrpcServer(@Named("Grpc.server.port") int port) {
		this.port = port;
		this.grpcServerBuilder = ServerBuilder.forPort(port);
	}
	
	@Override
	public void start() throws Exception {
		this.grpcServer = this.grpcServerBuilder.build().start();
		this.grpcServer.awaitTermination();
	}

	@Override
	public void stop() {
	    if (this.grpcServer != null) {
	    		this.grpcServer.shutdown();
	    }
	}

}
