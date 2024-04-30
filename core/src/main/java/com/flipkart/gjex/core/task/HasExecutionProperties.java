package com.flipkart.gjex.core.task;

public interface HasExecutionProperties {

    int getTimeout();

    boolean isWithRequestHedging();

    long getRollingTailLatency();

    String getName();
}
