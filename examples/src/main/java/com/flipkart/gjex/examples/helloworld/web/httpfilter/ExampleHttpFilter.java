package com.flipkart.gjex.examples.helloworld.web.httpfilter;

import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.ResponseParams;
import com.flipkart.gjex.core.filter.http.AccessLogHttpFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

public class ExampleHttpFilter extends AccessLogHttpFilter {

  @Override
  public void doProcessRequest(RequestParams<ServletRequest, Set<String>> requestParams) {
    setStartTime(System.currentTimeMillis());
    setSb(new StringBuilder());
    getSb().append("example-filter: ");
  }

  @Override
  public void doProcessResponseHeaders(Set<String> responseHeaders) {
    if (getRequest() instanceof HttpServletRequest && getResponse() instanceof HttpServletResponse){
      HttpServletRequest httpServletRequest= (HttpServletRequest) getRequest();
      HttpServletResponse httpServletResponse = (HttpServletResponse) getResponse();
      getSb().append(httpServletRequest.getHeader("x-forwarded-for")).append(" ")
          .append(httpServletRequest.getRequestURI()).append(" ")
          .append(httpServletResponse.getStatus()).append(" ")
          .append(httpServletResponse.getHeader("Content-Length")).append(" ");
    } else {
      getSb().append("Did not get HTTP request").append(" ");
    }
  }

  @Override
  public void doProcessResponse(ResponseParams<ServletResponse> response) {
    getSb().append(getRequest().getRemoteAddr()).append(" ");
    getSb().append(System.currentTimeMillis()-getStartTime());
    info("access-log", getSb().toString());
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}
}
