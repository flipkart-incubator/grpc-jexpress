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

import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.List;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.flipkart.gjex.core.Application;
import com.flipkart.gjex.core.Bundle;
import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.service.Service;
import com.google.common.collect.Lists;

/**
 * The pre-start application container, containing services required to bootstrap a GJEX application
 *
 * @author regu.b
 *
 */
public class Bootstrap implements Logging {

	private final Application application;
	private final MetricRegistry metricRegistry;
	private final List<Bundle> bundles;
	private ClassLoader classLoader;

	/** List of initialized Service instances*/
	List<Service> services;
	/** List of initialized Filter instances*/
	@SuppressWarnings("rawtypes")
	List<Filter> filters;

	/** The HealthCheckRegistry*/
	private HealthCheckRegistry healthCheckRegistry;

	public Bootstrap(Application application) {
		this.application = application;
		this.metricRegistry = new MetricRegistry();
		this.bundles = Lists.newArrayList();
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
	public Application getApplication() {
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
    public void addBundle(Bundle bundle) {
        bundle.initialize(this);
        bundles.add(bundle);
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

	@SuppressWarnings("rawtypes")
	public List<Filter> getFilters() {
		return filters;
	}
	
	/**
     * Runs this Bootstrap's bundles in the specified Environment
     * @param environment the Application Environment
     * @throws Exception in case of errors during run
     */
    @SuppressWarnings("rawtypes")
	public void run(Environment environment) throws Exception {
		// Identify all Service implementations, start them and register for Runtime shutdown hook
        this.services = new LinkedList<Service>();
        this.filters = new LinkedList<Filter>();
        // Set the HealthCheckRegsitry to the one initialized by the Environment
        this.healthCheckRegistry = environment.getHealthCheckRegistry();
        for (Bundle bundle : bundles) {
            bundle.run(environment);
            services.addAll(bundle.getServices());
            filters.addAll(bundle.getFilters());
            // Register all HealthChecks with the HealthCheckRegistry
            bundle.getHealthChecks().forEach(hc -> this.healthCheckRegistry.register(hc.getClass().getSimpleName(), hc));
        }
		this.services.forEach(service -> {
			try {
				service.start();
			} catch (Exception e) {
				error("Error starting a Service : " + service.getClass().getName(), e);
                throw new RuntimeException(e);
			}
		});
		this.filters.forEach(filter -> {
			try {
				filter.init();
			} catch (Exception e) {
				error("Error initializing a Filter : " + filter.getClass().getName(), e);
                throw new RuntimeException(e);
			}
		});
		this.registerServicesForShutdown();
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
    
}
