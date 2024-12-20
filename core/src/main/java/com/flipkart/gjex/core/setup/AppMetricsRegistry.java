package com.flipkart.gjex.core.setup;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import io.prometheus.metrics.instrumentation.dropwizard.DropwizardExports;
import io.prometheus.metrics.model.registry.PrometheusRegistry;

import java.lang.management.ManagementFactory;


public class AppMetricsRegistry {

    private final MetricRegistry metricRegistry;

    private final PrometheusRegistry prometheusRegistry;

    public AppMetricsRegistry(MetricRegistry metricRegistry, PrometheusRegistry prometheusRegistry) {
        this.metricRegistry = metricRegistry;
        this.prometheusRegistry = prometheusRegistry;
        registerJVMMetrics();
        registerPrometheusDropwizardExports();
        JmxReporter.forRegistry(metricRegistry).build().start();
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

    private void registerPrometheusDropwizardExports() {
        prometheusRegistry.register(new DropwizardExports(metricRegistry, MetricFilter.ALL));
    }

    private void registerJVMMetrics(){
        metricRegistry.register("jvm.buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
        metricRegistry.register("jvm.gc", new GarbageCollectorMetricSet());
        metricRegistry.register("jvm.memory", new MemoryUsageGaugeSet());
        metricRegistry.register("jvm.threads", new ThreadStatesGaugeSet());
    }

}
