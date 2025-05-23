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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * A gRPC Filter Config for processing filters
 *
 * @author ajay.jalgaonkar
 */

@NoArgsConstructor
@Getter
@Setter
public class GrpcFilterConfig {
    @JsonProperty("enableAccessLogs")
    private boolean enableAccessLogs = true;

    @JsonProperty("accessLogFormat")
    private String accessLogFormat = "{clientIp} - [{requestTime}] \"{method} {resourcePath}\" {responseStatus} {contentLength} {responseTime}";

    @JsonProperty("globalFilters")
    private List<String> globalFilterClasses;
}
