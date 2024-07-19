package com.flipkart.gjex.examples.helloworld.web.httpfilter;

import com.flipkart.gjex.core.web.filter.HttpFilterParams;

import javax.servlet.Filter;

public class ExampleHttpFilterParams extends HttpFilterParams {

  public ExampleHttpFilterParams(Filter filter, String pathSpec) {
    super(filter, pathSpec);
  }
}
