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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A gRPC Filter Config for processing filters
 *
 * @author ajay.jalgaonkar
 */

public class GrpcFilterConfig {
    @JsonProperty("enableAccessLogs")
    private boolean enableAccessLogs = true;

    @JsonProperty("accessLogFormat")
    private String accessLogFormat = "{clientIp} {resourcePath} {contentLength} - {responseTime}";

    public boolean isEnableAccessLogs() {
        return enableAccessLogs;
    }

    public void setEnableAccessLogs(boolean enableAccessLogs) {
        this.enableAccessLogs = enableAccessLogs;
    }

    public String getAccessLogFormat() {
        return accessLogFormat;
    }

    public void setAccessLogFormat(String accessLogFormat) {
        this.accessLogFormat = accessLogFormat;
    }
}
