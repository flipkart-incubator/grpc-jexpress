package com.flipkart.gjex.db;

import io.dropwizard.db.ManagedDataSource;
import com.codahale.metrics.MetricRegistry;
import io.dropwizard.util.Duration;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public interface PooledDataSourceFactory {

    boolean isAutoCommentsEnabled();

    /**
     * Returns the configuration properties for ORM tools.
     *
     * @return configuration properties as a map
     */
    Map<String, String> getProperties();

    /**
     * Returns the timeout for awaiting a response from the database
     * during connection health checks.
     *
     * @return the timeout as {@code Duration}
     */
    Optional<Duration> getValidationQueryTimeout();

    /**
     * Returns the timeout for awaiting a response from the database
     * during connection health checks.
     *
     * @return the timeout as {@code Duration}
     * @deprecated Use {@link #getValidationQueryTimeout()}
     */
    @Deprecated
    Optional<Duration> getHealthCheckValidationTimeout();

    /**
     * Returns the SQL query, which is being used for the database
     * connection health check.
     *
     * @return the SQL query as a string
     */
    Optional<String> getValidationQuery();

    /**
     * Returns the SQL query, which is being used for the database
     * connection health check.
     *
     * @return the SQL query as a string
     * @deprecated Use {@link #getValidationQuery()}
     */
    @Deprecated
    String getHealthCheckValidationQuery();

    /**
     * Returns the Java class of the database driver.
     *
     * @return the JDBC driver class as a string
     */
    @Nullable
    String getDriverClass();

    /**
     * Returns the JDBC connection URL.
     *
     * @return the JDBC connection URL as a string
     */
    String getUrl();

    /**
     * Configures the pool as a single connection pool.
     * It's useful for tools that use only one database connection,
     * such as database migrations.
     */
    void asSingleConnectionPool();

    ManagedDataSource build(MetricRegistry metricRegistry, String name);
}
