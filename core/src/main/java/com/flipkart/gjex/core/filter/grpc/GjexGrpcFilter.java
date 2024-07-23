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
package com.flipkart.gjex.core.filter.grpc;

import com.flipkart.gjex.core.filter.GjexFilter;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Metadata;

/**
 * A Filter interface for processing Request, Request-Headers, Response and Response-Headers around gRPC method invocation
 *  
 * @author ajay.jalgaonkar
 *
 * @param <Req> Proto V3 message
 * @param <Res> Proto V3 message
 */
public abstract class GjexGrpcFilter<Req extends GeneratedMessageV3, Res extends GeneratedMessageV3>
		extends GjexFilter<Req,Res, Metadata> {

	/** Lifecycle methods for initializing and cleaning up resources used by this Filter*/
	public void init(){}

	/**
	 * Function for creating an instance of this {@link GjexFilter}
	 * Use only this function to get the {@link GjexFilter} instance
	 */
	public abstract GjexGrpcFilter<Req, Res> getInstance();

	/**
	 * Returns array of {@link Metadata.Key} identifiers for headers to be forwarded
	 * @return array of {@link Metadata.Key}
	 */
	@SuppressWarnings("rawtypes")
	public Metadata.Key[] getForwardHeaderKeys(){
		return new Metadata.Key[] {};
	}
}
