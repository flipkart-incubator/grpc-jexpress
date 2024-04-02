package com.flipkart.gjex.core.setup;

import io.dropwizard.metrics5.MetricFilter;
import io.dropwizard.metrics5.MetricRegistry;
import io.prometheus.metrics.instrumentation.dropwizard5.DropwizardExports;
import io.prometheus.metrics.model.registry.PrometheusRegistry;


public class AppMetricsRegistry {

    private final MetricRegistry metricRegistry;

    private final PrometheusRegistry prometheusRegistry;

    public AppMetricsRegistry(MetricRegistry metricRegistry, PrometheusRegistry prometheusRegistry) {
        this.metricRegistry = metricRegistry;
        this.prometheusRegistry = prometheusRegistry;
        registerDropwizardMetrics();
    }

    private void registerDropwizardMetrics() {
        prometheusRegistry.register(new DropwizardExports(metricRegistry, MetricFilter.ALL));
    }

    public AppMetricsRegistry(MetricRegistry metricRegistry) {
        this(metricRegistry, PrometheusRegistry.defaultRegistry);
    }

    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    public PrometheusRegistry getPrometheusRegistry() {
        return prometheusRegistry;
    }
}
