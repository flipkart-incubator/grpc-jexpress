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
 * A convenience implementation of the {@link Filter} interface for concrete subtypes. 
 * @author regu.b
 *
 */
public abstract class AbstractFilter<Req extends GeneratedMessageV3, Res extends GeneratedMessageV3> implements Filter<Req, Res>{
	
	/**
	 * No-Op convenience implementations
	 */
	public void init() {}
	public void destroy() {}
	public void doFilterRequest(Req request, Metadata requestFilters) throws StatusRuntimeException {}	
}
