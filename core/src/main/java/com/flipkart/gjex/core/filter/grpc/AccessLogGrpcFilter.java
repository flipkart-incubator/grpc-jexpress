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

import com.flipkart.gjex.core.context.AccessLogContext;
import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.logging.Logging;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Metadata;
import lombok.Setter;
import org.slf4j.Logger;

/**
 * Implements a gRPC filter for logging access to gRPC services. This filter captures and logs
 * essential details such as the start time of the request, the client IP, the resource path,
 * and the size of the response message.
 * <p>
 * This class extends {@link GrpcFilter} to provide specific functionality for gRPC requests and responses.
 * It uses SLF4J for logging, facilitating integration with various logging frameworks.
 * </p>
 *
 * @param <R> The request type extending {@link GeneratedMessageV3}, representing the gRPC request message.
 * @param <S> The response type extending {@link GeneratedMessageV3}, representing the gRPC response message.
 * @author ajay.jalgaonkar
 */
public class AccessLogGrpcFilter<R extends GeneratedMessageV3, S extends GeneratedMessageV3>
    extends GrpcFilter<R, S> implements Logging {

    // The start time of the request processing.
    private long startTime;

    // Parameters of the request being processed, including client IP and resource path.
    private RequestParams<Metadata> requestParams;

    // The format string for the access log message.
    private static String format;

    // Logger instance for logging access log messages.
    private static final Logger logger = Logging.loggerWithName("ACCESS-LOG");

    public AccessLogGrpcFilter() {

    }

    public static void setFormat(String format) {
        AccessLogGrpcFilter.format = format;
    }

    /**
     * Processes the incoming gRPC request by initializing the start time and storing the request parameters.
     *
     * @param req                The incoming gRPC request message.
     * @param requestParamsInput Parameters of the request, including client IP and any additional metadata.
     */
    @Override
    public void doProcessRequest(R req, RequestParams<Metadata> requestParamsInput) {
        startTime = System.currentTimeMillis();
        requestParams = requestParamsInput;
    }

    /**
     * Placeholder method for processing response headers. Currently does not perform any operations.
     *
     * @param responseHeaders The metadata associated with the gRPC response.
     */
    @Override
    public void doProcessResponseHeaders(Metadata responseHeaders) {
    }

    /**
     * Processes the outgoing gRPC response by logging relevant request and response details.
     * Logs the client IP, requested resource path, size of the response message, and the time taken to process the request.
     *
     * @param response The outgoing gRPC response message.
     */
    @Override
    public void doProcessResponse(S response) {
        AccessLogContext accessLogContext = AccessLogContext.builder()
            .clientIp(requestParams.getClientIp())
            .resourcePath(requestParams.getResourcePath())
            .contentLength(response.getSerializedSize())
            .responseTime(System.currentTimeMillis() - startTime)
            .build();
        logger.info(accessLogContext.format(format));
    }

    @Override
    public void doHandleException(Exception e) {
        //Todo
    }

    /**
     * Provides an instance of this filter. This method facilitates the creation of new instances of the
     * AccessLogGrpcFilter for each gRPC call, ensuring thread safety and isolation of request data.
     *
     * @return A new instance of {@link AccessLogGrpcFilter}.
     */
    @Override
    public GrpcFilter<R, S> getInstance() {
        return new AccessLogGrpcFilter<>();
    }
}
