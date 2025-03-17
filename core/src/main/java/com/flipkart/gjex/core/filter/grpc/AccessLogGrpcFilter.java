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
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
@Named("AccessLogGrpcFilter")
public class AccessLogGrpcFilter<R extends GeneratedMessageV3, S extends GeneratedMessageV3>
    extends GrpcFilter<R, S> implements Logging {

    // The start time of the request processing.
    protected long startTime;

    // AccessLogContext of the request being processed
    protected AccessLogContext.AccessLogContextBuilder accessLogContextBuilder;

    // The format string for the access log message.
    protected static String format;

    // Logger instance for logging access log messages.
    private static final Logger logger = Logging.loggerWithName("ACCESS-LOG");

    public AccessLogGrpcFilter() {
        accessLogContextBuilder = AccessLogContext.builder();
        startTime = System.currentTimeMillis();
        accessLogContextBuilder.requestTime(startTime);
    }

    /**
     * Sets the format string for the access log message.
     *
     * @param format The format string to be used for logging.
     */
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
        accessLogContextBuilder
            .clientIp(requestParamsInput.getClientIp())
            .resourcePath(requestParamsInput.getResourcePath())
            .method(requestParamsInput.getMethod());

        Map<String, String> headers = requestParamsInput.getMetadata().keys().stream()
            .collect(Collectors.toMap(String::toLowerCase, key ->
                Optional.ofNullable(requestParamsInput.getMetadata().get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER))).orElse(StringUtils.EMPTY)
            ));

        accessLogContextBuilder.headers(headers);
    }

    /**
     * Placeholder method for processing response headers. Currently, does not perform any operations.
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
        accessLogContextBuilder
            .contentLength(response.getSerializedSize())
            .responseTime(System.currentTimeMillis() - startTime)
            .responseStatus(Status.Code.OK.value())
            .build();
        logger.info(accessLogContextBuilder.build().format(format));
    }

    /**
     * Handles exceptions that occur during the processing of the request or response.
     * Logs the exception details along with the request and response context.
     *
     * @param e The exception that occurred.
     */
    @Override
    public void doHandleException(Exception e) {
        if (e instanceof StatusRuntimeException){
            accessLogContextBuilder
                .responseStatus(((StatusRuntimeException) e).getStatus().getCode().value());
        } else {
            accessLogContextBuilder
                .responseStatus(Status.Code.INTERNAL.value());
        }
        accessLogContextBuilder
            .contentLength(0)
            .responseTime(System.currentTimeMillis() - startTime)
            .build();
        logger.info(accessLogContextBuilder.build().format(format));
    }


    @Override
    public GrpcFilter<R, S> configure(GrpcFilterConfig grpcFilterConfig) {
        if (grpcFilterConfig.isEnableAccessLogs()){
            AccessLogGrpcFilter accessLogGrpcFilter = new AccessLogGrpcFilter();
            if (StringUtils.isNotEmpty(grpcFilterConfig.getAccessLogFormat())){
                AccessLogGrpcFilter.setFormat(grpcFilterConfig.getAccessLogFormat());
            }
            return accessLogGrpcFilter;
        }

        return null;
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
