package com.flipkart.gjex.db;

import io.dropwizard.metrics5.health.HealthCheck;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class TimeBoundHealthCheck {
    private final ExecutorService executorService;
    private final Duration duration;

    public TimeBoundHealthCheck(ExecutorService executorService, Duration duration) {
        this.executorService = executorService;
        this.duration = duration;
    }

    public HealthCheck.Result check(Callable<HealthCheck.Result> c) {
        HealthCheck.Result result;
        try {
            result = (HealthCheck.Result)this.executorService.submit(c).get(this.duration.getQuantity(), this.duration.getUnit());
        } catch (Exception var4) {
            result = HealthCheck.Result.unhealthy("Unable to successfully check in %s", new Object[]{this.duration});
        }

        return result;
    }
}

