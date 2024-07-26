package com.flipkart.gjex.core.filter.http;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public abstract class FilterWrapper implements Filter {
  public abstract FilterWrapper getInstance();
}
