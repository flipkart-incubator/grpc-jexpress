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
package com.flipkart.gjex.core.web;

import com.flipkart.gjex.core.healthcheck.HealthCheckRegistry;
import io.dropwizard.metrics5.health.HealthCheck;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.SortedMap;

/**
 * Servlet Resource for the Dashboard HealthCheck API
 * @author ajay.jalgaonkar
 *
 */

@Singleton
@Path("/")
@Named("DashboardHealthCheckResource")
public class DashboardHealthCheckResource {

	@Context
	private ServletContext servletContext;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response performHealthChecks() {
    HealthCheckRegistry registry = (HealthCheckRegistry) servletContext
      .getAttribute(HealthCheckRegistry.HEALTHCHECK_REGISTRY_NAME);
    SortedMap<String, HealthCheck.Result> results = registry.runHealthChecks();
    if (results.values().stream().anyMatch(result -> !result.isHealthy())){
      return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(results).build();
    }
    return Response.status(Response.Status.OK).entity(results).build();
	}
}
