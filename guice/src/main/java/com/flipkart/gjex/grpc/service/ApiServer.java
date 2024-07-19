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
package com.flipkart.gjex.grpc.service;

import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.service.AbstractService;
import com.flipkart.gjex.core.service.Service;
import com.flipkart.gjex.core.web.filter.HttpFilterParams;
import com.flipkart.gjex.web.ResourceRegistrar;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

/**
 * <code>ApiServer</code> is a {@link Service} implementation that manages the GJEX API Jetty Server instance lifecycle
 * 
 * @author regunath.balasubramanian
 */

@Singleton
@Named("APIServer")
public class ApiServer extends AbstractService implements Logging {

	private final Server apiServer;
	private final ResourceRegistrar resourceRegistrar;
	private final ServletContextHandler context;
	private final Filter accessLogFilter;
	private List<ResourceConfig> resourceConfigs = new LinkedList<>();

	@Inject
	public ApiServer(@Named("APIJettyServer") Server apiServer,
									 @Named("ApiServletContext")ServletContextHandler context,
									 @Named("HttpAccessLogFilter")Filter accessLogFilter,
									 ResourceRegistrar resourceRegistrar) {
		this.apiServer = apiServer;
		this.context = context;
		this.accessLogFilter = accessLogFilter;
		this.resourceRegistrar = resourceRegistrar;
	}
	
	public void registerResources(List<ResourceConfig> resourceConfigs) {
		this.resourceConfigs.addAll(resourceConfigs);
	}

	public void registerHttpFilters(List<HttpFilterParams> httpFilterParamsList){
		context.addFilter(new FilterHolder(accessLogFilter), "/*" , EnumSet.of(DispatcherType.REQUEST));
		for (HttpFilterParams httpFilterParams : httpFilterParamsList){
			context.addFilter(new FilterHolder(httpFilterParams.getFilter()),
					httpFilterParams.getPathSpec(),
					EnumSet.of(DispatcherType.REQUEST));
		}
	}
	
	@Override
	public void doStart() throws Exception {
		this.resourceRegistrar.registerResources(this.resourceConfigs); // register any custom web resources added by the GJEX application
		this.apiServer.start();
		info("API Server started and listening on port : " + this.apiServer.getURI().getPort());
	}

	@Override
	public void doStop() {
		try {
			this.apiServer.stop();
		} catch (Exception e) {
			// Just log the error as we are stopping anyway
			error("Error stopping API server : " + e.getMessage(), e);
		}
	}

}
