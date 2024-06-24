package com.flipkart.gjex.core.filter;

public class ServerRequestParams {
  private String clientIp;
  private String methodName;

  public ServerRequestParams(String clientIp, String methodName) {
    this.clientIp = clientIp;
    this.methodName = methodName;
  }

  public String getClientIp() {
    return clientIp;
  }

  public String getMethodName() {
    return methodName;
  }
}
