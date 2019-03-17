package com.flipkart.gjex.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;

public class ApiService {

    @Min(1)
    @JsonProperty("service.port")
    private int port;

    @JsonProperty("service.acceptors")
    private int acceptors;

    @JsonProperty("service.selectors")
    private int selectors;

    @JsonProperty("service.workers")
    private int workers;

    @Min(1)
    @JsonProperty("scheduledexecutor.threadpool.size")
    private int scheduledExecutorThreadPoolSize;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getAcceptors() {
        return acceptors;
    }

    public void setAcceptors(int acceptors) {
        this.acceptors = acceptors;
    }

    public int getSelectors() {
        return selectors;
    }

    public void setSelectors(int selectors) {
        this.selectors = selectors;
    }

    public int getWorkers() {
        return workers;
    }

    public void setWorkers(int workers) {
        this.workers = workers;
    }

    public int getScheduledExecutorThreadPoolSize() {
        return scheduledExecutorThreadPoolSize;
    }

    public void setScheduledExecutorThreadPoolSize(int scheduledExecutorThreadPoolSize) {
        this.scheduledExecutorThreadPoolSize = scheduledExecutorThreadPoolSize;
    }
}
