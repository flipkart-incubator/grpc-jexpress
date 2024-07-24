package com.flipkart.gjex.core.filter.http;

import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.filter.RequestParams;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.servlet.FilterChain;
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

public abstract class HttpFilter extends Filter<ServletRequest, ServletResponse,
    Set<String>> implements javax.servlet.Filter {
  protected ServletRequest request;
  protected ServletResponse response;

  @Override
  public final void doFilter(ServletRequest requestInput, ServletResponse responseOutput,
                       FilterChain chain) throws IOException, ServletException {
    try {
      this.request = requestInput;
      this.response = responseOutput;
      RequestParams.RequestParamsBuilder<Set<String>> requestParamsBuilder =
          RequestParams.builder();
      if (request instanceof HttpServletRequest){
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        Set<String> headersNames = new HashSet<>(Collections.list(httpServletRequest.getHeaderNames()));
        requestParamsBuilder.metadata(headersNames);
      }
      doProcessRequest(request, requestParamsBuilder.build());
      chain.doFilter(request,response);
    } finally {
      if (response instanceof HttpServletResponse){
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        doProcessResponseHeaders(new HashSet<>(httpServletResponse.getHeaderNames()));
      }
      doProcessResponse(response);
    }
  }
}
