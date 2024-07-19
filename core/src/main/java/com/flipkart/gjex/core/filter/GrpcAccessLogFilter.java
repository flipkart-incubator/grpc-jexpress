package com.flipkart.gjex.core.filter;

import com.flipkart.gjex.core.logging.Logging;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Metadata;

public class GrpcAccessLogFilter <R extends GeneratedMessageV3,
    S extends GeneratedMessageV3> implements Filter<R, S>, Logging {
  private long startTime = 0;
  private ServerRequestParams serverRequestParams;
  @Override
  public Filter<R,S> getInstance(){
    return new GrpcAccessLogFilter<>();
  }

  @Override
  public void doProcessRequest(R request) {
    this.startTime = System.currentTimeMillis();
  }

  @Override
  public void doFilterRequest(ServerRequestParams serverRequestParams, Metadata headers) {
    this.serverRequestParams = serverRequestParams;
  }

  @Override
  public void doProcessResponseHeaders(Metadata responseHeaders) {}

  @Override
  public void doProcessResponse(S response) {
    String size = null;
    if (response != null){
      size = String.valueOf(response.getSerializedSize());
    }
    StringBuilder sb = new StringBuilder()
        .append(serverRequestParams.getClientIp()).append(" ")
        .append(serverRequestParams.getMethodName()).append(" ")
        .append(size).append(" ")
        .append(System.currentTimeMillis()-startTime);
    error(sb.toString());
  }
}
