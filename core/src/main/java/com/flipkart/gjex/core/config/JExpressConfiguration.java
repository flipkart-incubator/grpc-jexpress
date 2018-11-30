package com.flipkart.gjex.core.config;

import java.util.HashMap;
import java.util.Map;

public class JExpressConfiguration {
    private GrpcConfig grpc;
    private Map<String, Object> guiceBindableKeyVal = new HashMap<>();

    public GrpcConfig getGrpc() {
        return grpc;
    }

    public void setGrpc(GrpcConfig grpc) {
        this.grpc = grpc;
    }

    public Map<String, Object> getGuiceBindableKeyVal() {
        return guiceBindableKeyVal;
    }

    public void setGuiceBindableKeyVal(Map<String, Object> guiceBindableKeyVal) {
        this.guiceBindableKeyVal = guiceBindableKeyVal;
    }
}
