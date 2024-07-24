package com.flipkart.gjex.core.filter.grpc;

import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.ResponseParams;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Metadata;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Filter for logging grpc access log requests
 * @author ajay.jalgaonkar
 *
 */

@Data
@EqualsAndHashCode(callSuper=false)
public class AccessLogGrpcFilter<R extends GeneratedMessageV3,
    S extends GeneratedMessageV3> extends GrpcFilter<R,S> {
  private long startTime = 0;
  private RequestParams<R, Metadata> requestParams;
  private StringBuilder sb;

  @Override
  public void doProcessRequest(RequestParams<R, Metadata> requestParamsInput) {
    startTime = System.currentTimeMillis();
    requestParams = requestParamsInput;
    sb = new StringBuilder();
  }

  @Override
  public void doProcessResponseHeaders(Metadata responseHeaders) {}

  @Override
  public void doProcessResponse(ResponseParams<S> responseParams) {
    String size = null;
    if (responseParams.getResponse() != null){
      size = String.valueOf(responseParams.getResponse().getSerializedSize());
    }
    sb.append(requestParams.getClientIp()).append(" ")
      .append(requestParams.getResourcePath()).append(" ")
      .append(size).append(" ")
      .append(System.currentTimeMillis()-startTime);
    info("access-log", sb.toString());
  }

  @Override
  public GrpcFilter<R,S> getInstance(){
    return new AccessLogGrpcFilter<>();
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public RequestParams<R, Metadata> getRequestParams() {
    return requestParams;
  }

  public void setRequestParams(RequestParams<R, Metadata> requestParams) {
    this.requestParams = requestParams;
  }

  public StringBuilder getSb() {
    return sb;
  }

  public void setSb(StringBuilder sb) {
    this.sb = sb;
  }
}
