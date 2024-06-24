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

import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.logging.Logging;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Metadata;

import javax.inject.Named;

/**
 * An implementation of the {@link Filter} interface as example that simply logs Request information
 * @author regu.b
 */
@Named("LoggingFilter")
public class LoggingFilter<Req extends GeneratedMessageV3, Res extends GeneratedMessageV3> implements Filter<Req, Res>, Logging {

	/** Custom response key to indicate request was logged on the server*/
    static final Metadata.Key<String> CUSTOM_HEADER_KEY = Metadata.Key.of("request_response_logged_header_key", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public Filter<Req,Res> getNewInstance(){
        return new LoggingFilter<Req, Res>();
    }

    @Override
    public void doProcessRequest(Req request) {
        info("Logging from filter. Request payload is : " + request.toString());
    }

    @Override
    public void doProcessResponseHeaders(Metadata responseHeaders) {
        responseHeaders.put(CUSTOM_HEADER_KEY, "loggedRequestResponse");
    }

    @Override
    public void doProcessResponse(Res response) {
        info("Logging from filter. Response payload is : " + response.toString());
    }

}
