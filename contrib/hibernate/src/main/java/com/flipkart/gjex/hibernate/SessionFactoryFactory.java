package com.flipkart.gjex.hibernate;

import io.dropwizard.metrics5.MetricRegistry;
import com.flipkart.gjex.db.ManagedDataSource;
import com.flipkart.gjex.db.PooledDataSourceFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.*;

public class SessionFactoryFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionFactoryFactory.class);
    private static final String DEFAULT_NAME = "hibernate";

    public SessionFactoryFactory() {
    }

    public SessionFactory build(HibernateBundle<?, ?> bundle, MetricRegistry metricRegistry, PooledDataSourceFactory dbConfig, List<Class<?>> entities) {
        return this.build(bundle, metricRegistry, dbConfig, entities, "hibernate");
    }

    public SessionFactory build(HibernateBundle<?, ?> bundle, MetricRegistry metricRegistry, PooledDataSourceFactory dbConfig, List<Class<?>> entities, String name) {
        ManagedDataSource dataSource = dbConfig.build(metricRegistry, name);
        return this.build(bundle, metricRegistry, dbConfig, dataSource, entities);
    }

    public SessionFactory build(HibernateBundle<?, ?> bundle, MetricRegistry metricRegistry, PooledDataSourceFactory dbConfig, ManagedDataSource dataSource, List<Class<?>> entities) {
        ConnectionProvider provider = this.buildConnectionProvider(dataSource, dbConfig.getProperties());
        SessionFactory factory = this.buildSessionFactory(bundle, dbConfig, provider, dbConfig.getProperties(), entities);
        return factory;
    }

    private ConnectionProvider buildConnectionProvider(DataSource dataSource, Map<String, String> properties) {
        DatasourceConnectionProviderImpl connectionProvider = new DatasourceConnectionProviderImpl();
        connectionProvider.setDataSource(dataSource);
        connectionProvider.configure(properties);
        return connectionProvider;
    }

    private SessionFactory buildSessionFactory(HibernateBundle<?, ?> bundle, PooledDataSourceFactory dbConfig, ConnectionProvider connectionProvider, Map<String, String> properties, List<Class<?>> entities) {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.current_session_context_class", "managed");
        configuration.setProperty("hibernate.use_sql_comments", Boolean.toString(dbConfig.isAutoCommentsEnabled()));
        configuration.setProperty("hibernate.jdbc.use_get_generated_keys", "true");
        configuration.setProperty("hibernate.generate_statistics", "true");
        configuration.setProperty("hibernate.bytecode.use_reflection_optimizer", "true");
        configuration.setProperty("hibernate.order_updates", "true");
        configuration.setProperty("hibernate.order_inserts", "true");
        configuration.setProperty("hibernate.id.new_generator_mappings", "true");
        configuration.setProperty("jadira.usertype.autoRegisterUserTypes", "true");
        Iterator var7 = properties.entrySet().iterator();

        while (var7.hasNext()) {
            Map.Entry<String, String> property = (Map.Entry) var7.next();
            configuration.setProperty((String) property.getKey(), (String) property.getValue());
        }

        this.addAnnotatedClasses(configuration, entities);
        bundle.configure(configuration);
        ServiceRegistry registry = (new StandardServiceRegistryBuilder()).addService(ConnectionProvider.class, connectionProvider).applySettings(configuration.getProperties()).build();
        this.configure(configuration, registry);
        return configuration.buildSessionFactory(registry);
    }

    protected void configure(Configuration configuration, ServiceRegistry registry) {
    }

    private void addAnnotatedClasses(Configuration configuration, Iterable<Class<?>> entities) {
        SortedSet<String> entityClasses = new TreeSet();
        Iterator var4 = entities.iterator();

        while (var4.hasNext()) {
            Class<?> klass = (Class) var4.next();
            configuration.addAnnotatedClass(klass);
            entityClasses.add(klass.getCanonicalName());
        }

        LOGGER.info("Entity classes: {}", entityClasses);
    }
}

