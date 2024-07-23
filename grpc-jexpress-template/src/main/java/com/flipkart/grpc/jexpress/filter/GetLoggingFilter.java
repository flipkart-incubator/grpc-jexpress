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

import com.flipkart.gjex.core.filter.grpc.GjexGrpcFilter;
import com.flipkart.grpc.jexpress.GetRequest;
import com.flipkart.grpc.jexpress.GetResponse;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Metadata;
import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.ResponseParams;

import javax.inject.Named;

@Named("GetLoggingFilter")
public class GetLoggingFilter<GetRequest extends GeneratedMessageV3,
    GetResponse extends GeneratedMessageV3> extends GjexGrpcFilter<GetRequest, GetResponse> {

    public GetLoggingFilter(){}

    @Override
    public GjexGrpcFilter<GetRequest,GetResponse> getInstance(){
        return new GetLoggingFilter<GetRequest,GetResponse>();
    }

    @Override
    public void doProcessRequest(RequestParams<GetRequest, Metadata> requestParams) {
        info("Request: " + requestParams.getRequest());
    }

    @Override
    public void doProcessResponseHeaders(Metadata reponseHeaders) {
    }

    @Override
    public void doProcessResponse(ResponseParams<GetRequest> requestParams) {
        info("Response: " + responseParams.getResponse());
    }

}
