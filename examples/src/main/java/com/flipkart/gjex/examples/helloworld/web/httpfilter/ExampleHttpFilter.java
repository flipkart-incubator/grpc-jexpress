package com.flipkart.gjex.examples.helloworld.web.httpfilter;

import com.flipkart.gjex.core.filter.RequestParams;
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
  public void doProcessRequest(ServletRequest req, RequestParams<Set<String>> requestParams) {
    super.doProcessRequest(req, requestParams);
    setStartTime(System.currentTimeMillis());
  }

  @Override
  public void doProcessResponse(ServletResponse response) {
    super.doProcessResponse(response);
    logger.info("example filter: " + this.hashCode());
//    if (logger.isInfoEnabled()){
//      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//      HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//      logger.info("{} {} {} {} {} {}",
//          this.getClass().getSimpleName(),
//          requestParams.getClientIp(),
//          httpServletRequest.getRequestURI(),
//          httpServletResponse.getStatus(),
//          httpServletResponse.getHeader(CONTENT_LENGTH_HEADER),
//          System.currentTimeMillis()-startTime
//      );
//    }
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}
}
