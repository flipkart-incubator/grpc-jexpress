package com.flipkart.gjex.core.healthcheck;

import com.flipkart.gjex.core.filter.grpc.MethodFilters;
import io.dropwizard.metrics5.health.HealthCheck;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.stub.StreamObserver;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import java.util.SortedMap;

/**
 * Default GRPC HealthCheck Service
 * @author ajay.jalgaonkar
 */

@Singleton
@Named("GrpcHealthCheckService")
public class GrpcHealthCheckService extends HealthGrpc.HealthImplBase {

  @Context
  private ServletContext servletContext;

  public GrpcHealthCheckService(){

  }

  @Override
  @MethodFilters({})
  public void check(HealthCheckRequest request,
                    StreamObserver<HealthCheckResponse> responseObserver) {
    HealthCheckResponse.Builder builder = HealthCheckResponse.newBuilder();
    HealthCheckRegistry registry = (HealthCheckRegistry) servletContext
        .getAttribute(HealthCheckRegistry.HEALTHCHECK_REGISTRY_NAME);
    SortedMap<String, HealthCheck.Result> results = registry.runHealthChecks();
    if (results.values().stream().anyMatch(result -> !result.isHealthy())){
      builder.setStatus(HealthCheckResponse.ServingStatus.SERVING);
    } else {
      builder.setStatus(HealthCheckResponse.ServingStatus.NOT_SERVING);
    }
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }
}
