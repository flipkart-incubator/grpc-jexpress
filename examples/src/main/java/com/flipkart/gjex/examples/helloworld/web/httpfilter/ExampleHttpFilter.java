package com.flipkart.gjex.examples.helloworld.web.httpfilter;

import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.ResponseParams;
import com.flipkart.gjex.core.filter.http.HttpFilter;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

public class ExampleHttpFilter extends HttpFilter {
  private long startTime;
  private StringBuilder sb;

  @Override
  public void doProcessRequest(RequestParams<ServletRequest, Set<String>> requestParams) {
    startTime  = System.currentTimeMillis();
    sb = new StringBuilder();
    sb.append("example-filter: ");
  }

  @Override
  public void doProcessResponseHeaders(Set<String> responseHeaders) {
    if (getRequest() instanceof HttpServletRequest && getResponse() instanceof HttpServletResponse){
      HttpServletRequest httpServletRequest= (HttpServletRequest) getRequest();
      HttpServletResponse httpServletResponse = (HttpServletResponse) getResponse();
      sb.append(httpServletRequest.getHeader("x-forwarded-for")).append(" ")
          .append(httpServletRequest.getRequestURI()).append(" ")
          .append(httpServletResponse.getStatus()).append(" ")
          .append(httpServletResponse.getHeader("Content-Length")).append(" ");
    } else {
      sb.append("Did not get HTTP request").append(" ");
    }
  }

  @Override
  public void doProcessResponse(ResponseParams<ServletResponse> response) {
    sb.append(getRequest().getRemoteAddr()).append(" ");
    sb.append(System.currentTimeMillis()-startTime);
    info("access-log", sb.toString());
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}
}
