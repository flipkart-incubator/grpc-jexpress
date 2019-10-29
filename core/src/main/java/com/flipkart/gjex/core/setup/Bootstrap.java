/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flipkart.gjex.core.setup;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.gjex.core.Application;
import com.flipkart.gjex.core.Bundle;
import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.GJEXError;
import com.flipkart.gjex.core.GJEXObjectMapper;
import com.flipkart.gjex.core.config.ConfigurationException;
import com.flipkart.gjex.core.config.ConfigurationFactory;
import com.flipkart.gjex.core.config.ConfigurationFactoryFactory;
import com.flipkart.gjex.core.config.ConfigurationSourceProvider;
import com.flipkart.gjex.core.config.DefaultConfigurationFactoryFactory;
import com.flipkart.gjex.core.config.FileConfigurationSourceProvider;
import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.service.Service;
import com.flipkart.gjex.core.tracing.TracingSampler;
import com.flipkart.gjex.core.util.Pair;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * The pre-start application container, containing services required to bootstrap a GJEX application
 *
 * @author regu.b
 *
 */
@SuppressWarnings("rawtypes")
public class Bootstrap<T extends GJEXConfiguration, U extends Map> implements Logging {

	private final Application<T, U> application;
	private final MetricRegistry metricRegistry;
	private final List<Bundle<? super T, ? super U>> bundles;
	private final ObjectMapper objectMapper;
	private final String configPath;
	private final Class<T> configurationClass;

	private ClassLoader classLoader;

	private ConfigurationFactoryFactory<T, U> configurationFactoryFactory;
	private ConfigurationSourceProvider configurationSourceProvider;
	private ValidatorFactory validatorFactory;

	// these values are set in ConfigModule when GuiceBundle.initialize(this) gets called.
	private T configuration;
	private U configMap;

	/** List of initialized Service instances*/
	private List<Service> services;

	/** List of initialized Filter instances*/
	private List<Filter> filters;
	
	/** List of initialized ConfigurableTracingSampler instances*/
	private List<TracingSampler> tracingSamplers;

	/** The HealthCheckRegistry*/
	private HealthCheckRegistry healthCheckRegistry;
	
	public Bootstrap(Application<T, U> application, String configPath, Class<T> configurationClass) {
		this.configurationClass = configurationClass;
		this.configPath = configPath;
		this.application = application;
		this.metricRegistry = new MetricRegistry();
		this.bundles = Lists.newArrayList();
		this.objectMapper = GJEXObjectMapper.newObjectMapper();
		this.classLoader = Thread.currentThread().getContextClassLoader();
		this.configurationFactoryFactory = new DefaultConfigurationFactoryFactory<>();
		this.configurationSourceProvider = new FileConfigurationSourceProvider();
		this.validatorFactory = Validation.buildDefaultValidatorFactory();
		this.initializeConfig();

		getMetricRegistry().register("jvm.buffers", new BufferPoolMetricSet(ManagementFactory
                .getPlatformMBeanServer()));
		getMetricRegistry().register("jvm.gc", new GarbageCollectorMetricSet());
		getMetricRegistry().register("jvm.memory", new MemoryUsageGaugeSet());
		getMetricRegistry().register("jvm.threads", new ThreadStatesGaugeSet());
		JmxReporter.forRegistry(getMetricRegistry()).build().start();
	}
	
	/**
	 * Gets the bootstrap's Application
	 */
	public Application<T, U> getApplication() {
		return application;
	}

	/**
     * Returns the bootstrap's class loader.
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Sets the bootstrap's class loader.
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    /**
     * Adds the given bundle to the bootstrap.
     *
     * @param bundle a {@link Bundle}
     */
    public void addBundle(Bundle<? super T, ? super U> bundle) {
		Preconditions.checkState(bundles.isEmpty(),
				"bundles can be initialized only once");
        bundle.initialize(this);
        bundles.add(bundle);
    }

	public String getConfigPath() {
		return configPath;
	}

	/**
     * Returns the application's metrics.
     */
    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }
    
    public List<Service> getServices() {
		return services;
	}

	public List<Filter> getFilters() {
		return filters;
	}
	
	public List<TracingSampler> getTracingSamplers() {
		return tracingSamplers;
	}

	public ConfigurationFactoryFactory<T, U> getConfigurationFactoryFactory() {
		return configurationFactoryFactory;
	}

	public void setConfigurationFactoryFactory(ConfigurationFactoryFactory<T, U> configurationFactoryFactory) {
		this.configurationFactoryFactory = configurationFactoryFactory;
	}

	public ConfigurationSourceProvider getConfigurationSourceProvider() {
		return configurationSourceProvider;
	}

	public void setConfigurationSourceProvider(ConfigurationSourceProvider configurationSourceProvider) {
		this.configurationSourceProvider = configurationSourceProvider;
	}

	public ValidatorFactory getValidatorFactory() {
		return validatorFactory;
	}

	public void setValidatorFactory(ValidatorFactory validatorFactory) {
		this.validatorFactory = validatorFactory;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public Class<T> getConfigurationClass() {
		return configurationClass;
	}

	public T getConfiguration() {
		return configuration;
	}

	public void setConfiguration(T configuration) {
		this.configuration = configuration;
	}

	public U getConfigMap() {
		return configMap;
	}

	public void setConfigMap(U configMap) {
		this.configMap = configMap;
	}

	/**
     * Runs this Bootstrap's bundles in the specified Environment
     * @param environment the Application Environment
     * @throws Exception in case of errors during run
     */
	public void run(Environment environment) throws Exception {
		// Identify all Service implementations, start them and register for Runtime shutdown hook
        services = new LinkedList<Service>();
        filters = new LinkedList<Filter>();
        tracingSamplers = new LinkedList<TracingSampler>();
        // Set the HealthCheckRegsitry to the one initialized by the Environment
        healthCheckRegistry = environment.getHealthCheckRegistry();

        for (Bundle<? super T, ? super U> bundle : bundles) {
            bundle.run(configuration, configMap, environment);
            services.addAll(bundle.getServices());
            filters.addAll(bundle.getFilters());
            tracingSamplers.addAll(bundle.getTracingSamplers());
            // Register all HealthChecks with the HealthCheckRegistry
            bundle.getHealthChecks().forEach(hc -> this.healthCheckRegistry.register(hc.getClass().getSimpleName(), hc));
        }
		services.forEach(service -> {
			try {
				service.start();
			} catch (Exception e) {
				error("Error starting a Service : " + service.getClass().getName(), e);
                throw new RuntimeException(e);
			}
		});
		filters.forEach(filter -> {
			try {
				filter.init();
			} catch (Exception e) {
				error("Error initializing a Filter : " + filter.getClass().getName(), e);
                throw new RuntimeException(e);
			}
		});
		registerServicesForShutdown();
    }

    public HealthCheckRegistry getHealthCheckRegistry() {		
		return this.healthCheckRegistry;
	} 
        
    private void registerServicesForShutdown() throws Exception {
	    	Runtime.getRuntime().addShutdownHook(new Thread() {
	    		@Override
	    		public void run() {
	    			// Use stdout here since the logger may have been reset by its JVM shutdown hook.
	    			System.out.println("*** Shutting down GJEX server since JVM is shutting down");
	    			services.forEach(Service::stop);
	    			filters.forEach(Filter::destroy);
	    			System.out.println("*** Server shut down");
	    		}
	    	});    		
    }

    private void initializeConfig() {
		try {
			Pair<T, U> pair = parseConfiguration(this.getConfigurationFactoryFactory(), this.getConfigurationSourceProvider(),
					this.getValidatorFactory().getValidator(), this.getConfigPath(), this.getConfigurationClass(),
					this.getObjectMapper());
			this.setConfiguration(pair.getKey()); // NOTE
			this.setConfigMap(pair.getValue()); // NOTE
		} catch (ConfigurationException | IOException e) {
			throw new GJEXError(GJEXError.ErrorType.runtime, "Error occurred while reading/parsing configuration " +
					"from source " + this.getConfigPath(), e);
		}
	}

	private Pair<T, U> parseConfiguration(ConfigurationFactoryFactory<T, U> configurationFactoryFactory,
										  ConfigurationSourceProvider provider,
										  Validator validator,
										  String configPath,
										  Class<T> klass,
										  ObjectMapper objectMapper) throws IOException, ConfigurationException {
		final ConfigurationFactory<T, U> configurationFactory = configurationFactoryFactory
				.create(klass, validator, objectMapper);
		if (configPath != null) {
			return configurationFactory.build(provider, configPath);
		}
		return configurationFactory.build();
	}
}
