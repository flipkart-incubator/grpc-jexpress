package com.flipkart.gjex.core.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServerRequestParams {
  private String clientIp;
  private String methodName;
}
