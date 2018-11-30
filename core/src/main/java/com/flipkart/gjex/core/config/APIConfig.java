package com.flipkart.gjex.core.config;

public class APIConfig {
    private ServiceConfig service;
    private ScheduledExecutorConfig scheduledexecutor;

    public ServiceConfig getService() {
        return service;
    }

    public void setService(ServiceConfig service) {
        this.service = service;
    }

    public ScheduledExecutorConfig getScheduledexecutor() {
        return scheduledexecutor;
    }

    public void setScheduledexecutor(ScheduledExecutorConfig scheduledexecutor) {
        this.scheduledexecutor = scheduledexecutor;
    }
}
