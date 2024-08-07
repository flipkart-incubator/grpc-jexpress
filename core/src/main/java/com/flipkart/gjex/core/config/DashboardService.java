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
package com.flipkart.gjex.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DashboardService {

    @JsonProperty("service.port")
    private int port;

    @JsonProperty("service.acceptors")
    private int acceptors;

    @JsonProperty("service.selectors")
    private int selectors;

    @JsonProperty("service.workers")
    private int workers;

    @JsonProperty("healthcheck.path")
    private String healthCheckPath = "/healthcheck";
}
