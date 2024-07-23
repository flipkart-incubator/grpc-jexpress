package com.flipkart.gjex.core.filter;

import lombok.Builder;
import lombok.Getter;

/**
 * Wrapper for request of Http/Grpc Request
 * @author ajay.jalgaonkar
 *
 */

@Getter
@Builder
public class RequestParams<R,M> {
  String clientIp;
  String resourcePath;
  R request;
  M metadata;
}
