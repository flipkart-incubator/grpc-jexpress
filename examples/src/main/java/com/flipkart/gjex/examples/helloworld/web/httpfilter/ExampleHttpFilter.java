package com.flipkart.gjex.examples.helloworld.web.httpfilter;

import com.flipkart.gjex.core.filter.http.AccessLogHttpFilter;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExampleHttpFilter extends AccessLogHttpFilter {

  @Override
  public void doProcessResponse(ServletResponse response) {
    if (logger.isInfoEnabled()) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      HttpServletResponse httpServletResponse = (HttpServletResponse) response;
      logger.info("{} {} {} {} {} {}",
          this.getClass().getSimpleName(),
          getRequestParams().getClientIp(),
          httpServletRequest.getRequestURI(),
          httpServletResponse.getStatus(),
          httpServletResponse.getHeader(CONTENT_LENGTH_HEADER),
          System.currentTimeMillis() - getStartTime()
      );
      clearMDC();
    }
  }
}
