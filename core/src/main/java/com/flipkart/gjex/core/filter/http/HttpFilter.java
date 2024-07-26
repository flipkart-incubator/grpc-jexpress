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
import com.flipkart.gjex.core.filter.grpc.GrpcFilter;

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
public abstract class HttpFilter extends Filter<ServletRequest, ServletResponse, Set<String>> {

  /**
   * Function for creating an instance of this {@link Filter}
   * Use only this function to get the {@link Filter} instance
   */
  public abstract HttpFilter getInstance();

}