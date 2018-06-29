/*
 * Copyright 2018-2025, the original author or authors.
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

import javax.inject.Named;

import com.flipkart.gjex.core.filter.AbstractFilter;
import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.logging.Logging;

import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;

/**
 * An implementation of the {@link Filter} interface as example
 * @author regu.b
 *
 */
@Named("LoggingFilter")
public class LoggingFilter extends AbstractFilter<HelloRequest, HelloReply> implements Logging {

	@Override
	public void doFilterRequest(HelloRequest request, Metadata requestHeaders) throws StatusRuntimeException {
		info("Logging from filter. Request payload is : " + request.getName());
	}

}
