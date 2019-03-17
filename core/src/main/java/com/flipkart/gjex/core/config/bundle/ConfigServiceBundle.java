package com.flipkart.gjex.core.config.bundle;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.flipkart.gjex.core.Bundle;
import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.GJEXObjectMapper;
import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.service.Service;
import com.flipkart.gjex.core.setup.Bootstrap;
import com.flipkart.gjex.core.setup.Environment;
import com.flipkart.gjex.core.tracing.TracingSampler;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

// TODO - Move Config service related classes to "contrib" folder.
public class ConfigServiceBundle<T extends GJEXConfiguration, U extends Map> implements Bundle<T, U> {

    // this is the character which has been used as separator in Flattened Json  in ConfigService
    public static final char JSON_FLATTEN_SEPARATOR = '-';

    @Override
    public void initialize(Bootstrap<?, ?> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new ConfigServiceConfigurationSourceProvider(bootstrap.getObjectMapper(),
                                                        GJEXObjectMapper.newObjectMapper(new YAMLFactory())));
        bootstrap.setConfigurationFactoryFactory(new ConfigServiceConfigurationFactoryFactory<>());
    }

    @Override
    public void run(GJEXConfiguration configuration, Map configMap, Environment environment) {

    }

    @Override
    public List<Service> getServices() {
        return Lists.newArrayList();
    }

    @Override
    public List<Filter> getFilters() {
        return Lists.newArrayList();
    }

    @Override
    public List<HealthCheck> getHealthChecks() {
        return Lists.newArrayList();
    }

    @Override
    public List<TracingSampler> getTracingSamplers() {
        return Lists.newArrayList();
    }
}
