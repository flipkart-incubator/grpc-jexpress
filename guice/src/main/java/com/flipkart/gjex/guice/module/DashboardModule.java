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

package com.flipkart.gjex.guice.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.flipkart.gjex.Constants;
import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.healthcheck.HealthCheckRegistry;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.setup.Bootstrap;
import com.flipkart.gjex.core.tracing.TracingSamplerHolder;
import com.flipkart.gjex.core.web.DashboardHealthCheckResource;
import com.flipkart.gjex.core.web.DashboardResource;
import com.flipkart.gjex.core.web.HealthCheckResource;
import com.flipkart.gjex.core.web.RotationManagementResource;
import com.flipkart.gjex.core.web.TracingResource;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import io.dropwizard.metrics5.jetty9.InstrumentedHandler;
import io.prometheus.metrics.exporter.servlet.javax.PrometheusMetricsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.inject.Named;
import javax.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * <code>DashboardModule</code> is a Guice {@link AbstractModule} implementation used for wiring GJEX Dashboard components.
 *
 * @author regunath.balasubramanian
 */
@SuppressWarnings("rawtypes")
public class DashboardModule<T extends GJEXConfiguration, U extends Map> extends AbstractModule implements Logging {

	private final Bootstrap<T,U> bootstrap;

	public DashboardModule(Bootstrap<T,U> bootstrap) {
		this.bootstrap = bootstrap;
	}

	@Override
	protected void configure() {
		// do nothing
	}

	/**
	 * Creates the Jetty server instance for the admin Dashboard and configures it with the @Named("DashboardContext").
	 *
	 * @return Jetty Server instance
	 */
	@Named("DashboardJettyServer")
	@Provides
	@Singleton
	Server getDashboardJettyServer(@Named("Dashboard.service.port") int port,
								   @Named("DashboardResourceConfig")ResourceConfig resourceConfig,
									 @Named("DashboardHealthCheckResourceConfig")ResourceConfig dashboardHealthCheckResourceConfig,
								   @Named("Dashboard.service.acceptors") int acceptorThreads,
								   @Named("Dashboard.service.selectors") int selectorThreads,
								   @Named("Dashboard.service.workers") int maxWorkerThreads,
								   @Named("JSONMarshallingProvider")JacksonJaxbJsonProvider provider) {
		resourceConfig.register(provider);
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMaxThreads(maxWorkerThreads);
		Server server = new Server(threadPool);
		ServerConnector http = new ServerConnector(server, acceptorThreads, selectorThreads);
		http.setPort(port);
		server.addConnector(http);

		/** Initialize the Context and Servlet for serving static content */
		URL webRootLocation = this.getClass().getResource("/webroot/pages/dashboard.ftl");
		if (webRootLocation == null) {
			warn("Webroot location not found! Unable to find root location for Dashboard.");
		}
		ServletContextHandler context = new ServletContextHandler();
		try {
			URI webRootUri = URI
					.create(webRootLocation.toURI().toASCIIString().replaceFirst("/pages/dashboard.ftl$", "/"));
			context.setContextPath("/");
			context.setBaseResource(Resource.newResource(webRootUri));
			context.addServlet(DefaultServlet.class, "/");
		} catch (Exception e) {
			error("Unable to set resource base for Dashboard.", e);
		}
		context.getMimeTypes().addMimeMapping("txt", "text/plain;charset=utf-8");
		server.setHandler(context);

		context.setAttribute(HealthCheckRegistry.HEALTHCHECK_REGISTRY_NAME, this.bootstrap.getHealthCheckRegistry());
		/** Add the Servlet for serving the HealthCheck resource */
		context.addServlet(new ServletHolder(new ServletContainer(dashboardHealthCheckResourceConfig)),
            bootstrap.getConfiguration().getDashboardService().getHealthCheckPath());

		/** Add the Servlet for serving the Dashboard resource */
		context.addServlet(new ServletHolder(new ServletContainer(resourceConfig)), "/admin/*");

		/** Add the Hystrix metrics stream servlets */
		context.addServlet(HystrixMetricsStreamServlet.class, "/stream/hystrix.stream.command.local");
		context.addServlet(HystrixMetricsStreamServlet.class, "/stream/hystrix.stream.global");
		context.addServlet(HystrixMetricsStreamServlet.class, "/stream/hystrix.stream.tp.local");

		/** Add the promethus servlet */
		context.addServlet(PrometheusMetricsServlet.class,"/metrics");

		/** Add the Metrics instrumentation */
		final InstrumentedHandler handler = new InstrumentedHandler(this.bootstrap.getMetricRegistry());
		handler.setName("gjex-dashboard");
		handler.setHandler(context);
		server.setHandler(handler);

		server.setStopAtShutdown(true);
		return server;
	}

	/**
	 * Creates the Jetty server instance for the GJEX API endpoint.
	 * @return Jetty Server instance
	 */
	@Named("APIJettyServer")
	@Provides
	@Singleton
	Server getAPIJettyServer(@Named("APIVanillaJettyServer")Server server,
							@Named("ApiServletContext") ServletContextHandler context,
							@Named("HealthCheckResourceConfig") ResourceConfig healthCheckResourceConfig,
							@Named("RotationManagementResourceConfig") ResourceConfig rotationManagementResourceConfig,
							@Named("TracingResourceConfig")ResourceConfig tracingResourceConfig,
							@Named("TracingSamplerHolder")TracingSamplerHolder tracingSamplerHolder,
							@Named("JSONMarshallingProvider")JacksonJaxbJsonProvider provider) throws URISyntaxException, UnknownHostException {
		healthCheckResourceConfig.register(provider);
        String healthCheckPath =
            bootstrap.getConfiguration().getApiService().getHealthCheckPath();
		ServletHolder healthCheckServlet =
				new ServletHolder(new ServletContainer(healthCheckResourceConfig));
		context.addServlet(healthCheckServlet, healthCheckPath); // registering Health Check servlet under the /healthcheck path

		rotationManagementResourceConfig.register(provider);
		ServletHolder rotationManagementServlet =
				new ServletHolder(new ServletContainer(rotationManagementResourceConfig));
		context.addServlet(rotationManagementServlet, "/rotation/*"); // registering Rotation
		// Management servlet under the /rotation path

		tracingResourceConfig.register(provider);
		ServletHolder tracingServlet = new ServletHolder(new ServletContainer(tracingResourceConfig));
		context.addServlet(tracingServlet, "/tracingconfig"); // registering Tracing config servlet under the /tracingconfig path

		context.setAttribute(HealthCheckRegistry.HEALTHCHECK_REGISTRY_NAME, this.bootstrap.getHealthCheckRegistry());
		context.setAttribute(TracingSamplerHolder.TRACING_SAMPLER_HOLDER_NAME, tracingSamplerHolder);

		final InstrumentedHandler handler = new InstrumentedHandler(this.bootstrap.getMetricRegistry());
		handler.setName("gjex-api");
		handler.setHandler(context);
		server.setHandler(handler);

		return server;
	}

	/**
	 * Creates the vanilla Jetty server instance for the GJEX API endpoint.
	 * @return Jetty Server instance
	 */
	@Named("APIVanillaJettyServer")
	@Provides
	@Singleton
	Server getAPIVanillaJettyServer(@Named("Api.service.port") int port,
							 @Named("Api.service.acceptors") int acceptorThreads,
							 @Named("Api.service.selectors") int selectorThreads,
							 @Named("Api.service.workers") int maxWorkerThreads) throws URISyntaxException, UnknownHostException {
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMaxThreads(maxWorkerThreads);
		Server server = new Server(threadPool);
		ServerConnector http = new ServerConnector(server, acceptorThreads, selectorThreads);
		http.setPort(port);
		server.addConnector(http);
		server.setStopAtShutdown(true);
		return server;
	}

	@Named("JSONMarshallingProvider")
	@Singleton
	@Provides
	JacksonJaxbJsonProvider getJSONMarshallingProvider(ObjectMapper objectMapper) {
		JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
		provider.setMapper(objectMapper);
		return provider;
	}

	@Named("ApiServletContext")
	@Singleton
	@Provides
	public ServletContextHandler getApiServletContext(@Named("APIVanillaJettyServer")Server server) {
		return new ServletContextHandler(server, "/");
	}

	@Named("HealthCheckResourceConfig")
	@Singleton
	@Provides
	ResourceConfig getAPIResourceConfig() {
    HealthCheckResource healthCheckResource = new HealthCheckResource(bootstrap.getHealthCheckRegistry());
		ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.register(healthCheckResource);
		resourceConfig.setApplicationName(Constants.GJEX_CORE_APPLICATION);
		return resourceConfig;
	}

	@Named("DashboardHealthCheckResourceConfig")
	@Singleton
	@Provides
	ResourceConfig getDashboardHealthCheckResourceConfig() {
    DashboardHealthCheckResource dashboardHealthCheckResource =
      new DashboardHealthCheckResource(bootstrap.getHealthCheckRegistry());
		ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.register(dashboardHealthCheckResource);
		resourceConfig.setApplicationName(Constants.GJEX_CORE_APPLICATION);
		return resourceConfig;
	}

	@Named("RotationManagementResourceConfig")
	@Singleton
	@Provides
	ResourceConfig getRotationManagementResourceConfig(RotationManagementResource rotationManagementResource) {
		ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.register(rotationManagementResource);
		resourceConfig.setApplicationName(Constants.GJEX_CORE_APPLICATION);
		return resourceConfig;
	}

	@Named("TracingResourceConfig")
	@Singleton
	@Provides
	ResourceConfig getTracingResourceConfig(TracingResource tracingResource) {
		ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.register(tracingResource);
		resourceConfig.setApplicationName(Constants.GJEX_CORE_APPLICATION);
		return resourceConfig;
	}

	@Named("DashboardResourceConfig")
	@Singleton
	@Provides
	ResourceConfig getDashboardResourceConfig(DashboardResource dashboardResource) {
		ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.register(dashboardResource);
		resourceConfig.setApplicationName(Constants.GJEX_CORE_APPLICATION);
		resourceConfig.property(FreemarkerMvcFeature.TEMPLATES_BASE_PATH, "webroot/pages");
		resourceConfig.register(FreemarkerMvcFeature.class);
		return resourceConfig;
	}

}
