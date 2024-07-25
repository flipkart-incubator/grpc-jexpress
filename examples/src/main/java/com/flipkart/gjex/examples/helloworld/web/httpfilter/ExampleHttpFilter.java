package com.flipkart.gjex.examples.helloworld.web.httpfilter;

import com.flipkart.gjex.core.filter.http.AccessLogHttpFilter;
import com.flipkart.gjex.core.filter.http.HttpFilter;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ExampleHttpFilter extends AccessLogHttpFilter {

  @Override
  public HttpFilter getInstance() {
    return new ExampleHttpFilter();
  }

  @Override
  public void doProcessResponse(ServletResponse response) {
    if (logger.isInfoEnabled()){
      HttpServletResponse httpServletResponse = (HttpServletResponse) response;
      logger.info("{} {} {} {} {} {}",
          this.getClass().getSimpleName(),
          requestParams.getClientIp(),
          requestParams.getResourcePath(),
          httpServletResponse.getStatus(),
          httpServletResponse.getHeader(CONTENT_LENGTH_HEADER),
          System.currentTimeMillis()-startTime
      );
    }
  }
}
