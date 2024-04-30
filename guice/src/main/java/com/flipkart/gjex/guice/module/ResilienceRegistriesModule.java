package com.flipkart.gjex.guice.module;

import com.codahale.metrics.MetricRegistry;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.resilience4all.resilience4j.timer.TimerRegistry;
import com.google.inject.*;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;

public class ResilienceRegistriesModule extends AbstractModule implements Logging {

    @Provides
    @Singleton
    public CircuitBreakerRegistry providesCircuitBreakerRegistry() {
        return CircuitBreakerRegistry.ofDefaults();
    }

    @Provides
    @Singleton
    public ThreadPoolBulkheadRegistry providesTPBulkheadRegistry() {
        return ThreadPoolBulkheadRegistry.ofDefaults();
    }

    @Provides
    @Singleton
    public BulkheadRegistry providesBulkheadRegistry() {
        return BulkheadRegistry.ofDefaults();
    }

    @Provides
    @Singleton
    public RetryRegistry providesRetryRegistry() {
        return RetryRegistry.ofDefaults();
    }

    @Provides
    @Singleton
    public TimeLimiterRegistry providesTimeLimiterRegistry() {
        return TimeLimiterRegistry.ofDefaults();
    }

    @Provides
    @Singleton
    public TimerRegistry providesTimerRegistry(MetricRegistry metricRegistry) {
        return TimerRegistry.ofMetricRegistry(metricRegistry);
    }

}
