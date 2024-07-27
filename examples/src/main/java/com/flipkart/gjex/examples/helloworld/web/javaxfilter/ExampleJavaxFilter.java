package com.flipkart.gjex.examples.helloworld.web.javaxfilter;

import com.flipkart.gjex.core.logging.Logging;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Example filter extending {@link Filter}
 * @author ajay.jalgaonkar
 */
public class ExampleJavaxFilter implements Logging, Filter {
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
}
