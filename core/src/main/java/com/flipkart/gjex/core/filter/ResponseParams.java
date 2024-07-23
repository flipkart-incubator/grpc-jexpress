package com.flipkart.gjex.core.filter;

import lombok.Builder;
import lombok.Getter;

/**
 * Wrapper for response of Http/Grpc Request
 *
 * @author ajay.jalgaonkar
 */

@Getter
@Builder
public class ResponseParams <S>{
  S response;
}
