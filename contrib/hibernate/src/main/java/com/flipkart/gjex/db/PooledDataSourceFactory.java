package com.flipkart.gjex.db;

import io.dropwizard.metrics5.MetricRegistry;

import java.util.Map;
import java.util.Optional;

public interface PooledDataSourceFactory {
    boolean isAutoCommentsEnabled();

    Map<String, String> getProperties();

    Optional<Duration> getValidationQueryTimeout();

    /** @deprecated */
    @Deprecated
    Optional<Duration> getHealthCheckValidationTimeout();

    String getValidationQuery();

    /** @deprecated */
    @Deprecated
    String getHealthCheckValidationQuery();

    String getDriverClass();

    String getUrl();

    void asSingleConnectionPool();

    ManagedDataSource build(MetricRegistry var1, String var2);
}
