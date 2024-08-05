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
package com.flipkart.gjex.grpc.service;

import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.filter.grpc.GrpcFilter;
import com.flipkart.gjex.core.filter.grpc.GrpcFilterConfig;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.service.AbstractService;
import com.flipkart.gjex.core.service.Service;
import com.flipkart.gjex.core.tracing.TracingSampler;
import com.flipkart.gjex.grpc.interceptor.FilterInterceptor;
import com.flipkart.gjex.grpc.interceptor.StatusMetricInterceptor;
import com.flipkart.gjex.grpc.interceptor.TracingInterceptor;
import io.grpc.BindableService;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.internal.GrpcUtil;
import io.grpc.protobuf.services.ProtoReflectionService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.Executors;

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

    /** Default maximum message size allowed to be received on the server*/
    private int maxMessageSize = GrpcUtil.DEFAULT_MAX_MESSAGE_SIZE;

    /** The core Grpc Server instance and its builder*/
    private ServerBuilder<?> grpcServerBuilder;
    private Server grpcServer;

    /** The ServerInterceptors*/
    private FilterInterceptor filterInterceptor;
    private TracingInterceptor tracingInterceptor;
    private StatusMetricInterceptor statusMetricInterceptor;

    @Inject
    public GrpcServer(GJEXConfiguration configuration,
                    @Named("FilterInterceptor") FilterInterceptor filterInterceptor,
                    @Named("TracingInterceptor") TracingInterceptor tracingInterceptor,
                    @Named("StatusMetricInterceptor") StatusMetricInterceptor statusMetricInterceptor) {
        if (configuration.getGrpc().getPort() > 0) {
            this.port = configuration.getGrpc().getPort();
            info("Creating GrpcServer listening on port : " + port);
        }

        if (configuration.getGrpc().getMaxMessageSize() > 0) {
            this.maxMessageSize = configuration.getGrpc().getMaxMessageSize();
            info("Creating GrpcServer with maximum message size allowed : " + maxMessageSize);
        }
        this.grpcServerBuilder =  Grpc.newServerBuilderForPort(this.port,  InsecureServerCredentials.create()).maxInboundMessageSize(this.maxMessageSize);

        if (configuration.getGrpc().getExecutorThreads() > 0) {
            this.grpcServerBuilder.executor(
                    Executors.newFixedThreadPool(
                            configuration.getGrpc().getExecutorThreads(),
                            GrpcUtil.getThreadFactory("grpc-executor-%d", true)));
        }


        this.filterInterceptor = filterInterceptor;
        this.tracingInterceptor = tracingInterceptor;
        this.statusMetricInterceptor = statusMetricInterceptor;
    }

    @Override
    public void doStart() throws Exception {
        this.grpcServer = this.grpcServerBuilder.addService(ProtoReflectionService.newInstance()).build().start();
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

    public void registerFilters(@SuppressWarnings("rawtypes") List<GrpcFilter> grpcFilters, List<BindableService> services, GrpcFilterConfig grpcFilterConfig) {
        this.filterInterceptor.registerFilters(grpcFilters, services, grpcFilterConfig);
    }

    public void registerTracingSamplers(List<TracingSampler> samplers, List<BindableService> services) {
        this.tracingInterceptor.registerTracingSamplers(samplers, services);
    }

    public void registerResponseMeteredMethods(List<BindableService> services) {
        this.statusMetricInterceptor.registerMeteredMethods(services);
    }

    public void registerServices(List<BindableService> services) {
        services.forEach(service -> this.grpcServerBuilder.addService(ServerInterceptors.intercept(service,
                this.statusMetricInterceptor, this.tracingInterceptor, this.filterInterceptor)));
    }

}
