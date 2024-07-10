package com.flipkart.gjex.core.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Parameters for passing to filters
 * @author ajay.jalgaonkar
 *
 */

@AllArgsConstructor
@Getter
@Builder
public class ServerRequestParams {
  private String clientIp;
  private String methodName;
}
