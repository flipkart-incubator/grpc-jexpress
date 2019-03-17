package com.flipkart.gjex.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GrpcConfig {

    @JsonProperty("server.port")
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
