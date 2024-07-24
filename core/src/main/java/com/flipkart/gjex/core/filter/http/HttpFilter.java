/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flipkart.gjex.core.filter.http;

import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.filter.RequestParams;

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
 * Abstract base class for HTTP filters that process requests and responses around HTTP method invocations.
 * This class provides a framework for capturing and manipulating HTTP request and response objects,
 * including headers and other metadata. It extends the generic {@link Filter} interface to work specifically
 * with HTTP requests and responses.
 * <p>
 * Implementations of this class should provide specific processing logic by overriding the
 * {@link #doProcessRequest(ServletRequest, RequestParams)} and {@link #doProcessResponse(ServletResponse)}
 * methods.
 * </p>
 *
 * @param <ServletRequest>  The ServletRequest object that contains the client's request
 * @param <ServletResponse> The ServletResponse object that contains the filter's response
 * @param <Set<String>>     The type of metadata associated with the request, typically a set of header names
 * @author ajay.jalgaonkar
 */
public abstract class HttpFilter extends Filter<ServletRequest, ServletResponse, Set<String>>
        implements javax.servlet.Filter {

  // The ServletRequest object that contains the client's request
  protected ServletRequest request;
  // The ServletResponse object that contains the filter's response
  protected ServletResponse response;

  /**
   * The core method that processes incoming requests and responses. It captures the request and response objects,
   * extracts and builds request parameters including client IP and request headers, and invokes the
   * {@link #doProcessRequest(ServletRequest, RequestParams)} method for further processing.
   * Finally, it ensures that the response is processed by invoking {@link #doProcessResponse(ServletResponse)}.
   *
   * @param requestInput  The incoming ServletRequest
   * @param responseOutput The outgoing ServletResponse
   * @param chain         The filter chain to which the request and response should be passed for further processing
   * @throws IOException      if an I/O error occurs during the filter chain execution
   * @throws ServletException if the request could not be handled
   */
  @Override
  public final void doFilter(ServletRequest requestInput, ServletResponse responseOutput,
                             FilterChain chain) throws IOException, ServletException {
    try {
      this.request = requestInput;
      this.response = responseOutput;
      RequestParams.RequestParamsBuilder<Set<String>> requestParamsBuilder = RequestParams.builder();
      if (request instanceof HttpServletRequest){
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        Set<String> headersNames = new HashSet<>(Collections.list(httpServletRequest.getHeaderNames()));
        requestParamsBuilder.metadata(headersNames);
        requestParamsBuilder.clientIp(getClientIp(request));
        requestParamsBuilder.resourcePath(httpServletRequest.getRequestURI());
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

  /**
   * Utility method to extract the real client IP address from the ServletRequest. It checks for the
   * "X-Forwarded-For" header to support clients connecting through a proxy.
   *
   * @param request The ServletRequest object containing the client's request
   * @return The real IP address of the client
   */
  private String getClientIp(ServletRequest request) {
    String remoteAddr = request.getRemoteAddr();
    String xForwardedFor = ((HttpServletRequest) request).getHeader("X-Forwarded-For");
    if (xForwardedFor != null) {
      remoteAddr = xForwardedFor.split(",")[0];
    }
    return remoteAddr;
  }
}