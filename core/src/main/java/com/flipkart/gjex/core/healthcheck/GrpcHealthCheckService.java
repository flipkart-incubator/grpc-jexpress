package com.flipkart.gjex.core.healthcheck;

import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.filter.grpc.MethodFilters;
import com.flipkart.gjex.core.setup.Bootstrap;
import io.dropwizard.metrics5.health.HealthCheck;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.stub.StreamObserver;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.SortedMap;

/**
 * Default GRPC HealthCheck Service
 * @author ajay.jalgaonkar
 */

@Singleton
@Named("GrpcHealthCheckService")
public class GrpcHealthCheckService<T extends GJEXConfiguration, U extends Map> extends HealthGrpc.HealthImplBase {

  private final Bootstrap<T,U> bootstrap;

  public GrpcHealthCheckService(Bootstrap<T, U> bootstrap){
    this.bootstrap = bootstrap;
  }

  @Override
  @MethodFilters({})
  public void check(HealthCheckRequest request,
                    StreamObserver<HealthCheckResponse> responseObserver) {
    HealthCheckResponse.Builder builder = HealthCheckResponse.newBuilder();
    SortedMap<String, HealthCheck.Result> results = bootstrap.getHealthCheckRegistry().runHealthChecks();
    if (results.values().stream().anyMatch(result -> !result.isHealthy())){
      builder.setStatus(HealthCheckResponse.ServingStatus.NOT_SERVING);
    } else {
      builder.setStatus(HealthCheckResponse.ServingStatus.SERVING);
    }
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }
}
