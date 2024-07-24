package com.flipkart.gjex.core.filter.http;

import com.flipkart.gjex.core.filter.RequestParams;
import org.slf4j.Logger;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * Filter for logging http access log requests
 * @author ajay.jalgaonkar
 *
 */

public class AccessLogHttpFilter extends HttpFilter {
  protected long startTime;
  protected Logger logger = getLoggerWithName("ACCESS-LOG");
  protected static final String CONTENT_LENGTH_HEADER = "Content-Length";
  protected static final String X_FORWARDED_FOR_HEADER = "x-forwarded-for";

  @Override
  public void doProcessRequest(ServletRequest req, RequestParams<Set<String>> requestParamsInput) {
    startTime  = System.currentTimeMillis();
  }

  @Override
  public void doProcessResponse(ServletResponse response) {
    if (logger.isInfoEnabled()){
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      HttpServletResponse httpServletResponse = (HttpServletResponse) response;
      logger.info("{} {} {} {} {} {}",
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

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }
}
