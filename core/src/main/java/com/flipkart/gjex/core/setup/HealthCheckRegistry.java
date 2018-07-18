/*
 * Copyright 2012-2016, the original author or authors.
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

import java.util.SortedMap;
import java.util.concurrent.ExecutorService;

import com.codahale.metrics.health.HealthCheck;

/**
 * A sub-type of the com.codahale.metrics.health.HealthCheckRegistry that runs health checks concurrently
 * @author regu.b
 *
 */
public class HealthCheckRegistry extends com.codahale.metrics.health.HealthCheckRegistry {
	
	/** Name for this HealthCheckRegistry*/
	public static final String HEALTHCHECK_REGISTRY_NAME = "GJEX_HealthCheckRegistry";
	
	private ExecutorService executorService;
	
    public HealthCheckRegistry(ExecutorService executorService) {
    		this.executorService = executorService;
	}

    /**
     * Runs HealthChecks concurrently using the ExecutorService
     */
    @Override
    public SortedMap<String, HealthCheck.Result> runHealthChecks() {
    		return this.runHealthChecks(this.executorService);
    }
}
