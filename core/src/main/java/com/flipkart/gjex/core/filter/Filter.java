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

import io.grpc.StatusException;

/**
 * A Filter interface for processing Request, Request-Headers, Response and Response-Headers
 * around gRPC and HTTP method invocation
 *
 * @author regu.b
 */
public abstract class Filter<Req, Res, M> {

    /**
     * Lifecycle method for initializing resources used by this Filter.
     */
    public void init() {}

    /**
     * Lifecycle method for cleaning up resources used by this Filter.
     */
    public void destroy() {}

    /**
     * Call-back to decorate or inspect the Request body/message. This Filter cannot fail processing of the Request body and hence there is no support for indicating failure.
     * This method should be viewed almost like a proxy for the Request body.
     *
     * @param req           the Request body/message
     * @param requestParams prams
     * @throws StatusException if the filter rejects the request based on gRPC status codes
     * TODO: change grpc.StatusException to something internal
     */
    public void doProcessRequest(Req req, RequestParams<M> requestParams) throws StatusException {}

    /**
     * Call-back to decorate or inspect the Response headers. Implementations may use this method to set additional headers in the response.
     *
     * @param responseHeaders the Response Headers
     */
    public void doProcessResponseHeaders(M responseHeaders) {}

    /**
     * Call-back to decorate or inspect the Response body/message. This Filter cannot fail processing of the Response body and hence there is no support for indicating failure.
     * This method should be viewed almost like a proxy for the Response body.
     *
     * @param response the Response body/message
     */
    public void doProcessResponse(Res response) {}

    /**
     * Call-back to handle exceptions that occur during the processing of the request or response.
     *
     * @param e The exception that occurred.
     */
    public void doHandleException(Exception e) {}
}
