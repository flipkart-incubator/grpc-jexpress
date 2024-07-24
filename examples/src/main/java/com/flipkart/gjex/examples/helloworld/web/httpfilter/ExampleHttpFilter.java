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
  }

  @Override
  public void doProcessResponse(ResponseParams<ServletResponse> response) {
    if (logger.isInfoEnabled()){
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      HttpServletResponse httpServletResponse = (HttpServletResponse) response.getResponse();
      logger.info("{} {} {} {} {} {} {}",
          this.getClass().getSimpleName(),
          httpServletRequest.getHeader(X_FORWARDED_FOR_HEADER),
          httpServletRequest.getRequestURI(),
          httpServletResponse.getStatus(),
          httpServletResponse.getHeader(CONTENT_LENGTH_HEADER),
          request.getRemoteAddr(),
          System.currentTimeMillis()-startTime
      );
    }
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}
}
