package com.flipkart.gjex.core.web.filter;

import lombok.Data;

import javax.servlet.Filter;

/**
 * Parameters for creating http filters
 * @author ajay.jalgaonkar
 *
 */

@Data
public class HttpFilterParams {
  private final Filter filter;
  private final String pathSpec;
}
