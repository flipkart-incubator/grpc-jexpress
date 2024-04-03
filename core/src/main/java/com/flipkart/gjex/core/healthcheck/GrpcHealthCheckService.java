package com.flipkart.gjex.core.healthcheck;

import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import io.grpc.stub.StreamObserver;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Default GRPC HealthCheck Service
 * @author ajaypj, See <a href="https://www.linkedin.com/in/apj">https://www.linkedin.com/in/apj</a>
 */

@Singleton
@Named("GrpcHealthCheckService")
public class GrpcHealthCheckService extends HealthGrpc.HealthImplBase {
  private RotationManagementBasedHealthCheck rotationManagementBasedHealthCheck;

  @Inject
  public GrpcHealthCheckService(RotationManagementBasedHealthCheck rotationManagementBasedHealthCheck){
    this.rotationManagementBasedHealthCheck = rotationManagementBasedHealthCheck;
  }

  @Override
  public void check(HealthCheckRequest request,
                    StreamObserver<HealthCheckResponse> responseObserver) {
    HealthCheckResponse.Builder builder = HealthCheckResponse.newBuilder();
    if (rotationManagementBasedHealthCheck.inRotation()) {
      builder.setStatus(HealthCheckResponse.ServingStatus.SERVING);
    } else {
      builder.setStatus(HealthCheckResponse.ServingStatus.NOT_SERVING);
    }
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }
}
