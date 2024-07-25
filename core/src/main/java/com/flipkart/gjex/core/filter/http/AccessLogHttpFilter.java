package com.flipkart.gjex.core.filter.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.gjex.core.GJEXObjectMapper;
import com.flipkart.gjex.core.filter.RequestParams;
import org.slf4j.Logger;
import org.slf4j.MDC;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * Implements an HTTP filter for logging access requests. This filter captures and logs
 * essential request and response details such as client IP, request URI, response status,
 * content length, and the time taken to process the request.
 * <p>
 * This class extends {@link HttpFilter} to provide specific logging functionality for HTTP requests.
 * It uses SLF4J for logging, allowing for integration with various logging frameworks.
 * </p>
 *
 * @author ajay.jalgaonkar
 */
public class AccessLogHttpFilter extends HttpFilter {
  private final String UUID = java.util.UUID.randomUUID().toString();
  private static final String START_TIME = "START_TIME";
  private static final String REQUEST_PARAMS = "REQUEST_PARAMS";
  private static final ObjectMapper objectMapper = GJEXObjectMapper.newObjectMapper();

  // Logger instance for logging access log messages.
  protected Logger logger = loggerWithName("ACCESS-LOG");

  // HTTP header name for content length.
  protected static final String CONTENT_LENGTH_HEADER = "Content-Length";

  /**
   * Processes the incoming request by initializing the start time and storing the request parameters.
   *
   * @param req The incoming servlet request.
   * @param requestParamsInput Parameters of the request, including client IP and any additional metadata.
   */
  @Override
  public void doProcessRequest(ServletRequest req, RequestParams<Set<String>> requestParamsInput) {
    setStartTime(System.currentTimeMillis());
    setRequestParams(requestParamsInput);
  }

  /**
   * Processes the outgoing response by logging relevant request and response details.
   * Logs the client IP, requested URI, response status, content length, and the time taken to process the request.
   *
   * @param response The outgoing servlet response.
   */
  @Override
  public void doProcessResponse(ServletResponse response) {
    if (logger.isInfoEnabled()) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      HttpServletResponse httpServletResponse = (HttpServletResponse) response;
      logger.info("{} {} {} {} {}",
            getRequestParams().getClientIp(),
            httpServletRequest.getRequestURI(),
            httpServletResponse.getStatus(),
            httpServletResponse.getHeader(CONTENT_LENGTH_HEADER),
            System.currentTimeMillis() - getStartTime()
      );
      clearMDC();
    }
  }

  /**
   * Initializes the filter with the given filter configuration. This method is a placeholder
   * and does not perform any initialization logic in the current implementation.
   *
   * @param filterConfig The filter configuration provided by the servlet container.
   * @throws ServletException if an error occurs during initialization.
   */
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  protected long getStartTime() {
    return Long.parseLong(MDC.get(UUID + START_TIME));
  }

  protected void setStartTime(long startTime) {
    MDC.put(UUID + START_TIME, String.valueOf(startTime));
  }

  protected RequestParams<Set<String>> getRequestParams() {
    String requestParams = MDC.get(UUID + REQUEST_PARAMS);
    if (requestParams != null){
      try {
        return objectMapper.readValue(requestParams, new TypeReference<RequestParams<Set<String>>>() {});
      } catch (JsonProcessingException e) {
        error("unable to deserialize requestParams: " + requestParams);
        throw new RuntimeException(e);
      }
    }
    return null;
  }

  protected void setRequestParams(RequestParams<Set<String>> requestParams) {
    try {
      MDC.put(UUID + REQUEST_PARAMS, objectMapper.writeValueAsString(requestParams));
    } catch (JsonProcessingException e) {
      error("unable to serialize requestParams: " + requestParams);
      throw new RuntimeException(e);
    }
  }

  protected void clearMDC(){
    MDC.remove(UUID + START_TIME);
    MDC.remove(UUID + REQUEST_PARAMS);
  }
}