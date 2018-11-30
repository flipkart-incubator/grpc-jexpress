package com.flipkart.gjex.core.config;

public class GrpcConfig {
    private ServerConfig server;
    private DashboardConfig dashboard;
    private APIConfig api;
    private TracingConfig tracing;

    public ServerConfig getServer() {
        return server;
    }

    public void setServer(ServerConfig server) {
        this.server = server;
    }

    public DashboardConfig getDashboard() {
        return dashboard;
    }

    public void setDashboard(DashboardConfig dashboard) {
        this.dashboard = dashboard;
    }

    public APIConfig getApi() {
        return api;
    }

    public void setApi(APIConfig api) {
        this.api = api;
    }

    public TracingConfig getTracing() {
        return tracing;
    }

    public void setTracing(TracingConfig tracing) {
        this.tracing = tracing;
    }
}
