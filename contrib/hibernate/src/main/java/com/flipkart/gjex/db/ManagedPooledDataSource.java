package com.flipkart.gjex.db;

import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.metrics5.MetricRegistry;
import org.apache.tomcat.jdbc.pool.DataSourceProxy;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;

import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class ManagedPooledDataSource extends DataSourceProxy implements ManagedDataSource {

    private final MetricRegistry metricRegistry;

    public ManagedPooledDataSource(PoolConfiguration config, MetricRegistry metricRegistry) {
        super(config);
        this.metricRegistry = metricRegistry;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Doesn't use java.util.logging");
    }

}
