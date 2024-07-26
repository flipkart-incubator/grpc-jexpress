package com.flipkart.gjex.examples.helloworld.web.javaxfilter;

import com.flipkart.gjex.core.filter.http.FilterWrapper;
import com.flipkart.gjex.core.logging.Logging;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class ExampleJavaxFilter extends FilterWrapper implements Logging {
  public AtomicInteger number = new AtomicInteger();

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    info("printing from ExampleJavaxFilter: " + number.getAndIncrement());
  }

  @Override
  public void destroy() {

  }

  @Override
  public FilterWrapper getInstance() {
    return new ExampleJavaxFilter();
  }
}
