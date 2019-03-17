package com.flipkart.gjex.core.config;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class Tracing {

    @NotNull
    @JsonProperty("collector.endpoint")
    private String collectorEndpoint;

    public String getCollectorEndpoint() {
        return collectorEndpoint;
    }

    public void setCollectorEndpoint(String collectorEndpoint) {
        this.collectorEndpoint = collectorEndpoint;
    }
}
