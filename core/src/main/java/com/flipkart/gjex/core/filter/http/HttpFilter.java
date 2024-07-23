package com.flipkart.gjex.core.filter.http;

import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.ResponseParams;
import lombok.Data;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An Http Filter extending {@link Filter}  interface for processing Request, Request-Headers,
 * Response and
 * Response-Headers
 * around HTTP method invocation
 *
 * @author ajay.jalgaonkar
 */
@Data
public abstract class HttpFilter extends Filter<ServletRequest, ServletResponse,
    Set<String>> implements javax.servlet.Filter {
  private ServletRequest request;
  private ServletResponse response;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public final void doFilter(ServletRequest requestInput, ServletResponse responseOutput,
                       FilterChain chain) throws IOException, ServletException {
    try {
      this.request = requestInput;
      this.response = responseOutput;
      RequestParams.RequestParamsBuilder<ServletRequest, Set<String>> requestParamsBuilder =
          RequestParams.builder();
      requestParamsBuilder.request(request);
      if (request instanceof HttpServletRequest){
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        Set<String> headersNames = new HashSet<>(Collections.list(httpServletRequest.getHeaderNames()));
        requestParamsBuilder.metadata(headersNames);
      }
      doProcessRequest(requestParamsBuilder.build());
      chain.doFilter(request,response);
    } finally {
      if (response instanceof HttpServletResponse){
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        doProcessResponseHeaders(new HashSet<>(httpServletResponse.getHeaderNames()));
      }
      doProcessResponse(ResponseParams.<ServletResponse>builder().response(response).build());
    }
  }

  @Override
  public void doProcessRequest(RequestParams<ServletRequest, Set<String>> requestParams) {
    super.doProcessRequest(requestParams);
  }

  @Override
  public void doProcessResponseHeaders(Set<String> responseHeaders) {
    super.doProcessResponseHeaders(responseHeaders);
  }

  @Override
  public void doProcessResponse(ResponseParams<ServletResponse> responseParams) {
    super.doProcessResponse(responseParams);
  }
}
