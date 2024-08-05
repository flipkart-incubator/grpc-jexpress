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

import com.flipkart.gjex.core.filter.http.AccessLogHttpFilter;
import com.flipkart.gjex.core.filter.http.HttpFilterConfig;
import com.flipkart.gjex.core.filter.http.HttpFilterParams;
import com.flipkart.gjex.core.filter.http.JavaxFilterParams;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.service.AbstractService;
import com.flipkart.gjex.core.service.Service;
import com.flipkart.gjex.http.interceptor.HttpFilterInterceptor;
import com.flipkart.gjex.web.ResourceRegistrar;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.DispatcherType;
import java.util.ArrayList;
import java.util.EnumSet;
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
	private HttpFilterInterceptor httpFilterInterceptor;
	private List<ResourceConfig> resourceConfigs = new ArrayList<>();
  private static String accessLogFormat = "{clientIp} {resourcePath} {contentLength} {responseStatus} {responseTime}";

	@Inject
	public ApiServer(@Named("APIJettyServer") Server apiServer,
									 @Named("ApiServletContext") ServletContextHandler context,
									 @Named("HttpFilterInterceptor") HttpFilterInterceptor httpFilterInterceptor,
									 ResourceRegistrar resourceRegistrar) {
		this.apiServer = apiServer;
		this.context = context;
		this.httpFilterInterceptor = httpFilterInterceptor;
		this.resourceRegistrar = resourceRegistrar;
	}

	public void registerResources(List<ResourceConfig> resourceConfigs) {
		this.resourceConfigs.addAll(resourceConfigs);
	}

	public void registerFilters(List<HttpFilterParams> httpFilterParamsList,
                                List<JavaxFilterParams> javaxFilterParamsList,
                                HttpFilterConfig httpFilterConfig){
		configureAccessLog(httpFilterParamsList, httpFilterConfig);
		httpFilterInterceptor.registerFilters(httpFilterParamsList);
		context.addFilter(new FilterHolder(httpFilterInterceptor), "/*", EnumSet.of(DispatcherType.REQUEST));
        for (JavaxFilterParams javaxFilterParams: javaxFilterParamsList){
            context.addFilter(new FilterHolder(javaxFilterParams.getFilter()), javaxFilterParams.getPathSpec(), EnumSet.of(DispatcherType.REQUEST));
        }
	}

	private void configureAccessLog(List<HttpFilterParams> httpFilterParamsList, HttpFilterConfig httpFilterConfig){
		if (httpFilterConfig.isEnableAccessLogs()){
      if (StringUtils.isNotEmpty(httpFilterConfig.getAccessLogFormat())) {
        accessLogFormat = httpFilterConfig.getAccessLogFormat();
      }
      httpFilterParamsList.add(0, HttpFilterParams.builder()
        .filter(new AccessLogHttpFilter(accessLogFormat)).pathSpec("/*").build());
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
