package com.flipkart.gjex.core.web.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.servlet.Filter;

/**
 * Parameters for creating http filters
 * @author ajay.jalgaonkar
 *
 */

@AllArgsConstructor
@Getter
@Builder
public class HttpFilterParams {
  private final Filter filter;
  private final String pathSpec;
}
