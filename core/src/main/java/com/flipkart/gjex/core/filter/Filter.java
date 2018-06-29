/*
 * Copyright 2012-2016, the original author or authors.
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
import io.grpc.StatusRuntimeException;

/**
 * A Filter interafce for processing Request, Request-Headers, Response and Response-Headers around gRPC method invocation
 *  
 * @author regu.b
 *
 * @param <Req> Proto V3 message
 * @param <Res> Proto V3 message
 */
public interface Filter<Req extends GeneratedMessageV3, Res extends GeneratedMessageV3> {
	public void init();
	public void destroy();
	public void doFilterRequest(Req request, Metadata requestHeaders) throws StatusRuntimeException;
}
