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

import com.google.protobuf.GeneratedMessageV3;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

/**
 * A Filter interface for processing Request, Request-Headers, Response and Response-Headers around gRPC method invocation
 *  
 * @author regu.b
 *
 * @param <Req> Proto V3 message
 * @param <Res> Proto V3 message
 */
public interface Filter<Req extends GeneratedMessageV3, Res extends GeneratedMessageV3> {
	
	/** Lifecycle methods for initializing and cleaning up resources used by this Filter*/
	default void init() {}
	default void destroy() {}
	
	/**
	 * Call-back to process Request headers and Filter out processing of the next incoming Request Proto V3 body/message. 
	 * @param requestHeaders Request Headers
	 * @throws StatusRuntimeException thrown with suitable {@link Status} to indicate reason for failing the request
	 */
	default void doFilterRequest(Metadata requestHeaders) throws StatusRuntimeException{}
	
	/**
	 * Call-back to decorate or inspect the Reauest Proto V3 body/message. This Filter cannot fail processing of the Request body and hence there is no support for indicating failure.
	 * This method should be viewed almost like a proxy for the Request body.
	 * @param request the Request Proto V3 body/message
	 */
	default void doProcessRequest(Req request) {}
	
	/**
	 * Call-back to decorate or inspect the Response headers. Implementations may use this method to set additional headers in the response.
	 * @param responseHeaders the Response Headers
	 */
	default void doProcessResponseHeaders(Metadata responseHeaders) {}
	
	/**
	 * Call-back to decorate or inspect the Response Proto V3 body/message. This Filter cannot fail processing of the Response body and hence there is no support for indicating failure.
	 * This method should be viewed almost like a proxy for the Response body.
	 * @param response the Response Proto V3 body/message
	 */
	default void doProcessResponse(Res response) {}
	
	/**
	 * Returns array of {@link Key} identifiers for headers to be forwarded
	 * @return array of {@link Key}
	 */
	@SuppressWarnings("rawtypes")
	default Metadata.Key[] getForwardHeaderKeys(){
		return new Metadata.Key[] {};
	}
}
