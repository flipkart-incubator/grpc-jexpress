package com.flipkart.gjex.core.filter.grpc;

import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.ResponseParams;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Metadata;
import org.slf4j.Logger;

/**
 * Filter for logging grpc access log requests
 * @author ajay.jalgaonkar
 *
 */

public class AccessLogGrpcFilter<R extends GeneratedMessageV3, S extends GeneratedMessageV3> extends GrpcFilter<R,S> {
  protected long startTime;
  protected RequestParams<R, Metadata> requestParams;
  protected Logger logger = getLoggerWithName("ACCESS-LOG");

  @Override
  public void doProcessRequest(RequestParams<R, Metadata> requestParamsInput) {
    startTime = System.currentTimeMillis();
    requestParams = requestParamsInput;
  }

  @Override
  public void doProcessResponseHeaders(Metadata responseHeaders) {}

  @Override
  public void doProcessResponse(ResponseParams<S> responseParams) {
    String size = null;
    if (responseParams.getResponse() != null){
      size = String.valueOf(responseParams.getResponse().getSerializedSize());
    }
    if (logger.isInfoEnabled()){
      logger.info("{} {} {} {}", requestParams.getClientIp(), requestParams.getResourcePath(),
          size, System.currentTimeMillis()-startTime);
    }
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
}
