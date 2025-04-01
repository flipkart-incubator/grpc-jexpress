package com.flipkart.gjex.hibernate;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.flipkart.gjex.core.Bundle;
import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.filter.grpc.GrpcFilter;
import com.flipkart.gjex.core.filter.http.HttpFilter;
import com.flipkart.gjex.core.job.ScheduledJob;
import com.flipkart.gjex.core.service.Service;
import com.flipkart.gjex.core.setup.Bootstrap;
import com.flipkart.gjex.core.setup.Environment;
import com.flipkart.gjex.core.tracing.TracingSampler;
import com.flipkart.gjex.db.DatabaseConfiguration;
import com.google.common.collect.ImmutableList;
import io.dropwizard.db.PooledDataSourceFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.hibernate.SessionFactory;

import java.util.*;

public abstract class HibernateBundle<T extends GJEXConfiguration, U extends Map> implements Bundle<T, U>, DatabaseConfiguration {

    public static final String DEFAULT_NAME = "hibernate";
    private final List<Class<?>> entities;
    private final SessionFactoryFactory sessionFactoryFactory;
    private SessionFactory sessionFactory;
    private boolean lazyLoadingEnabled;

    protected HibernateBundle(Class<?> entity, Class<?>... entities) {
        final List<Class<?>> entityClasses = new ArrayList<>();
        entityClasses.add(entity);
        entityClasses.addAll(Arrays.asList(entities));

        this.entities = Collections.unmodifiableList(entityClasses);
        this.sessionFactoryFactory = new SessionFactoryFactory();
    }

    protected HibernateBundle(ImmutableList<Class<?>> entities, SessionFactoryFactory sessionFactoryFactory) {
        this.lazyLoadingEnabled = true;
        this.entities = entities;
        this.sessionFactoryFactory = sessionFactoryFactory;
    }

    public final void initialize(Bootstrap<?, ?> bootstrap) {
        bootstrap.getObjectMapper().registerModule(this.createHibernate5Module());
        PooledDataSourceFactory dbConfig = this.getDataSourceFactory(bootstrap.getConfiguration());
        this.sessionFactory = this.sessionFactoryFactory.build(this, bootstrap.getMetricRegistry(), dbConfig, this.entities, this.name());
    }

    protected Hibernate5Module createHibernate5Module() {
        Hibernate5Module module = new Hibernate5Module();
        if (this.lazyLoadingEnabled) {
            module.enable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);
        }

        return module;
    }

    protected String name() {
        return DEFAULT_NAME;
    }


    @Override
    public void run(T t, U u, Environment environment) {

    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    protected void configure(org.hibernate.cfg.Configuration configuration) {

    }

    @Override
    public List<Service> getServices() {
        return Collections.emptyList();
    }

    @Override
    public List<HealthCheck> getHealthChecks() {
        return Collections.emptyList();
    }

    @Override
    public List<TracingSampler> getTracingSamplers() {
        return Collections.emptyList();
    }

    @Override
    public List<ResourceConfig> getResourceConfigs() {
        return Collections.emptyList();
    }

    @Override
    public List<GrpcFilter> getGrpcFilters() {
        return Collections.emptyList();
    }

    @Override
    public List<HttpFilter> getHTTPFilters() {
        return Collections.emptyList();
    }

    @Override
    public List<ScheduledJob> getScheduledJobs() {
        return Collections.emptyList();
    }
}
