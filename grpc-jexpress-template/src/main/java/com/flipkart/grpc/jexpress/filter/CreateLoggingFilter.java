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
package com.flipkart.grpc.jexpress.filter;


import com.flipkart.gjex.core.filter.grpc.GrpcFilter;
import com.flipkart.grpc.jexpress.CreateRequest;
import com.flipkart.grpc.jexpress.CreateResponse;
import com.google.protobuf.GeneratedMessageV3;
import com.flipkart.gjex.core.filter.RequestParams;
import io.grpc.Metadata;
import com.flipkart.gjex.core.logging.Logging;

import javax.inject.Named;

@Named("CreateLoggingFilter")
public class CreateLoggingFilter<CreateRequest extends GeneratedMessageV3,CreateResponse extends GeneratedMessageV3>
    extends GrpcFilter<CreateRequest, CreateResponse> implements Logging {

    @Override
    public GrpcFilter<CreateRequest,CreateResponse> getInstance(){
        return new CreateLoggingFilter<CreateRequest,CreateResponse>();
    }

    @Override
    public void doProcessRequest(CreateRequest request, RequestParams<Metadata> requestParams) {
        info("Request: " + request);
    }

    @Override
    public void doProcessResponseHeaders(Metadata responseHeaders) {}

    @Override
    public void doProcessResponse(CreateResponse response) {
        info("Response: " + response);
    }

}
