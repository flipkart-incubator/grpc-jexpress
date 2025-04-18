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
package com.flipkart.gjex.examples.helloworld.filter;

import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.grpc.GrpcFilter;
import com.flipkart.gjex.core.filter.grpc.GrpcFilterConfig;
import com.flipkart.gjex.core.logging.Logging;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;

import javax.inject.Named;

/**
 * An implementation of the {@link GrpcFilter} interface as example that performs naive authentication based on
 * information contained in the Request headers
 *
 * @author regu.b
 *
 */
@Named("AuthFilter")
public class AuthFilter extends GrpcFilter<HelloRequest, HelloReply> implements Logging {

	/** Fictitious authentication key*/
	@SuppressWarnings("rawtypes")
	static final Metadata.Key AUTH_KEY = Metadata.Key.of("DUMMY_AUTH_TOKEN", Metadata.ASCII_STRING_MARSHALLER);

	/** Flag to control authentication check*/
	private final boolean isAuth = false;

	@Override
	public GrpcFilter<HelloRequest, HelloReply> getInstance(){
		return new AuthFilter();
	}

	@Override
	public void doProcessRequest(HelloRequest request, RequestParams<Metadata> requestParams) throws StatusRuntimeException {
		info("Headers found in the request : " + requestParams.getMetadata().toString());
		this.checkAuth(requestParams.getMetadata());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Metadata.Key[] getForwardHeaderKeys() {
		return new  Metadata.Key[] {AUTH_KEY};
	}

	@SuppressWarnings("unchecked")
	private void checkAuth(Metadata requestHeaders) throws StatusRuntimeException {
		if (this.isAuth() && requestHeaders.get(AUTH_KEY) == null) {
			error("Rejecting this request as it is unauthenticated!");
			throw new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("Auth token is missing in request headers!"), requestHeaders);
		}
	}

	public boolean isAuth() {
		return this.isAuth;
	}

}

