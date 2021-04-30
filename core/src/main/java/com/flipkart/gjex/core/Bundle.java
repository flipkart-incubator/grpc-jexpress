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

package com.flipkart.gjex.core;

import java.util.List;
import java.util.Map;

import com.flipkart.gjex.core.job.ScheduledJob;
import org.glassfish.jersey.server.ResourceConfig;

import com.codahale.metrics.health.HealthCheck;
import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.service.Service;
import com.flipkart.gjex.core.setup.Bootstrap;
import com.flipkart.gjex.core.setup.Environment;
import com.flipkart.gjex.core.tracing.TracingSampler;

/**
 * A reusable bundle of functionality, used to define blocks of application behavior.
 */
@SuppressWarnings("rawtypes")
public interface Bundle<T extends GJEXConfiguration, U extends Map> {
	
    /**
     * Initializes this Bundle with the application bootstrap.
     *
     * @param bootstrap the application bootstrap
     */
    void initialize(Bootstrap<?, ?> bootstrap);

    /**
     * Runs this Bundle in the application environment.
     *
     * @param environment the application environment
     */
    void run(T configuration, U configMap, Environment environment);
    
    /**
     * Returns Service instances loaded by this Bundle
     * @return List containing Service instances
     */
    List<Service> getServices();
    
    /**
     * Returns Filter instances loaded by this Bundle
     * @return List containing Filter instances
     */
	List<Filter> getFilters();
    
    /**
     * Returns HealthCheck instances loaded by this Bundle
     * @return List containing HealthCheck instances
     */
    List<HealthCheck> getHealthChecks();
    
    /**
     * Returns the TracingSampler instances loaded by this Bundle
     * @return the TracingSampler instances
     */
    List<TracingSampler> getTracingSamplers();

    /**
     * Returns the ScheduledJob instances loaded by this Bundle
     * @return the ScheduledJob instances
     */
    List<ScheduledJob> getScheduledJobs();

    /**
     * Returns list of custom {@link ResourceConfig} instances configured by the GJEX application
     * @return ResourceConfig instances
     */
    List<ResourceConfig> getResourceConfigs();
}
