package com.flipkart.gjex.examples.helloworld.web.httpfilter;

import com.flipkart.gjex.core.logging.Logging;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExampleFilter implements Filter, Logging {
  private long startTime;
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain chain) throws IOException, ServletException {
    startTime = System.currentTimeMillis();
    chain.doFilter(request, response);
    StringBuilder sb = new StringBuilder();
    sb.append("example filter ");
    if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      HttpServletResponse httpServletResponse = (HttpServletResponse) response;
      sb.append(httpServletRequest.getHeader("x-forwarded-for")).append(" ")
          .append(httpServletRequest.getRequestURI()).append(" ")
          .append(httpServletResponse.getStatus()).append(" ")
          .append(httpServletResponse.getHeader("Content-Length")).append(" ");
    } else {
      sb.append("Did not get HTTP request").append(" ");
    }
    sb.append(request.getRemoteAddr()).append(" ");
    sb.append(System.currentTimeMillis() - startTime);
    error(sb.toString());
  }

  @Override
  public void destroy() {

  }
}
