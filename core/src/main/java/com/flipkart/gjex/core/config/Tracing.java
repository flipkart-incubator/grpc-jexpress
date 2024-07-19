package com.flipkart.gjex.core.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Tracing {

    @NotNull
    @JsonProperty("collector.endpoint")
    private String collectorEndpoint;
}
