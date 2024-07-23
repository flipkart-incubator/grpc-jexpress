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
package com.flipkart.gjex.core.filter;

import com.flipkart.gjex.core.logging.Logging;

/**
 * A Filter interface for processing Request, Request-Headers, Response and Response-Headers 
 * around gRPC and HTTP method invocation
 *
 * @author ajay.jalgaonkar
 */
public abstract class GjexFilter<Req, Res, M> implements Logging {

  /** Lifecycle methods for cleaning up resources used by this Filter*/
  public void destroy(){}

  /**
   * Call-back to decorate or inspect the Request body/message. This Filter cannot fail processing of the Request body and hence there is no support for indicating failure.
   * This method should be viewed almost like a proxy for the Request body.
   * @param requestParams the Request body/message
   */
  public void doProcessRequest(RequestParams<Req, M> requestParams){}

  /**
   * Call-back to decorate or inspect the Response headers. Implementations may use this method to set additional headers in the response.
   * @param responseHeaders the Response Headers
   */
  public void doProcessResponseHeaders(M responseHeaders) {}

  /**
   * Call-back to decorate or inspect the Response body/message. This Filter cannot fail processing of the Response body and hence there is no support for indicating failure.
   * This method should be viewed almost like a proxy for the Response body.
   * @param responseParams the Response body/message
   */
  public void doProcessResponse(ResponseParams<Res> responseParams) {}
}
