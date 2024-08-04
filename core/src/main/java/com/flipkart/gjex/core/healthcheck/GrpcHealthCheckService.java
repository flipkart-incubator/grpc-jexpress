package com.flipkart.gjex.core.healthcheck;

import com.flipkart.gjex.core.filter.grpc.AccessLogGrpcFilter;
import com.flipkart.gjex.core.filter.grpc.MethodFilters;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.stub.StreamObserver;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Default GRPC HealthCheck Service
 * @author ajay.jalgaonkar
 */

@Singleton
@Named("GrpcHealthCheckService")
public class GrpcHealthCheckService extends HealthGrpc.HealthImplBase {

  public GrpcHealthCheckService(){

  }

  @Override
  @MethodFilters({AccessLogGrpcFilter.class})
  public void check(HealthCheckRequest request,
                    StreamObserver<HealthCheckResponse> responseObserver) {
    HealthCheckResponse.Builder builder = HealthCheckResponse.newBuilder();
    if (RotationManagementBasedHealthCheck.inRotation()) {
      builder.setStatus(HealthCheckResponse.ServingStatus.SERVING);
    } else {
      builder.setStatus(HealthCheckResponse.ServingStatus.NOT_SERVING);
    }
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }
}
