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
package com.flipkart.gjex.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.gjex.core.config.ApiService;
import com.flipkart.gjex.core.config.DashboardService;
import com.flipkart.gjex.core.config.GrpcConfig;
import com.flipkart.gjex.core.config.Tracing;
import com.flipkart.gjex.core.filter.grpc.AuthConfig;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class GJEXConfiguration {

    @Valid
    @NotNull
    @JsonProperty("Grpc")
    private GrpcConfig grpc;

    @Valid
    @NotNull
    @JsonProperty("Api")
    private ApiService apiService;

    @Valid
    @NotNull
    @JsonProperty("Dashboard")
    private DashboardService dashboardService;

    @Valid
    @NotNull
    @JsonProperty("AuthConfig")
    private AuthConfig authConfig;

    @Valid
    @NotNull
    @JsonProperty("Tracing")
    private Tracing tracing;

    @Min(0)
    @JsonProperty("ScheduledJobs.executorThreads")
    private int scheduledJobExecutorThreads;



    @Override
    public String toString() {
        return "GJEXConfiguration{" +
                "grpc=" + grpc +
                ", apiService=" + apiService +
                ", dashboardService=" + dashboardService +
                ", tracing=" + tracing +
                ", scheduledJobExecutorThreads=" + scheduledJobExecutorThreads +
                '}';
    }
}
