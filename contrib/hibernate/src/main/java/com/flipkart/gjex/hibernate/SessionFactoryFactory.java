package com.flipkart.gjex.hibernate;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Environment;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import java.util.List;

public class SessionFactoryFactory extends io.dropwizard.hibernate.SessionFactoryFactory {

    public SessionFactory build(HibernateBundle<?> bundle, MetricRegistry metricRegistry, PooledDataSourceFactory dbConfig, List<Class<?>> entities) {
        return this.build(bundle, metricRegistry, dbConfig, entities, "hibernate");
    }

    public SessionFactory build(HibernateBundle<?> bundle, MetricRegistry metricRegistry, PooledDataSourceFactory dbConfig, List<Class<?>> entities, String name) {
        ManagedDataSource dataSource = dbConfig.build(metricRegistry, name);
        return this.build(bundle, metricRegistry, dbConfig, dataSource, entities);
    }

    public SessionFactory build(HibernateBundle<?> bundle, MetricRegistry metricRegistry, PooledDataSourceFactory dbConfig, ManagedDataSource dataSource, List<Class<?>> entities) {
        Environment environment =  new Environment(
                "hibernate",
                null,
                null,
                metricRegistry,
            null
        );
        return this.build(bundle, environment, dbConfig, dataSource, entities);
    }


}
