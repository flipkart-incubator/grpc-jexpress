package com.flipkart.gjex.core.healthcheck;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HttpHealthCheckConfig {
    @JsonProperty("healthCheckPath")
    private String healthCheckPath = "/healthcheck";
}
