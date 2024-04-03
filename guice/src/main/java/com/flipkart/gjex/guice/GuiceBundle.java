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
package com.flipkart.gjex.guice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.flipkart.gjex.core.job.ScheduledJob;
import com.flipkart.gjex.grpc.service.ScheduledJobManager;
import io.grpc.health.v1.HealthGrpc;
import org.glassfish.jersey.server.ResourceConfig;

import io.dropwizard.metrics5.health.HealthCheck;
import com.flipkart.gjex.core.Bundle;
import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.service.Service;
import com.flipkart.gjex.core.setup.Bootstrap;
import com.flipkart.gjex.core.setup.Environment;
import com.flipkart.gjex.core.tracing.TracingSampler;
import com.flipkart.gjex.grpc.service.ApiServer;
import com.flipkart.gjex.grpc.service.GrpcServer;
import com.flipkart.gjex.guice.module.ApiModule;
import com.flipkart.gjex.guice.module.ConfigModule;
import com.flipkart.gjex.guice.module.DashboardModule;
import com.flipkart.gjex.guice.module.GJEXEnvironmentModule;
import com.flipkart.gjex.guice.module.ServerModule;
import com.flipkart.gjex.guice.module.TaskModule;
import com.flipkart.gjex.guice.module.TracingModule;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.palominolabs.metrics.guice.MetricsInstrumentationModule;

import io.grpc.BindableService;
import ru.vyarus.guice.validator.ImplicitValidationModule;

/**
 * A Guice GJEX Bundle implementation. Multiple Guice Modules may be added to this Bundle.
 * 
 * @author regu.b
 *
 */
@SuppressWarnings("rawtypes")
public class GuiceBundle<T extends GJEXConfiguration, U extends Map> implements Bundle<T, U>, Logging {

	private List<Module> modules;
	private Injector baseInjector;
	private List<Service> services;
	private List<Filter> filters;
	private List<HealthCheck> healthchecks;
	private List<TracingSampler> tracingSamplers;
	private List<ScheduledJob> scheduledJobs;
	private List<ResourceConfig> resourceConfigs;
	private Optional<Class<T>> configurationClass;
	private GJEXEnvironmentModule gjexEnvironmentModule;

	public static class Builder<T extends GJEXConfiguration, U extends Map> {

		private List<Module> modules = Lists.newArrayList();
		private Optional<Class<T>> configurationClass = Optional.empty();

		public Builder<T, U> addModules(Module... moreModules) {
			for (Module module : moreModules) {
				Preconditions.checkNotNull(module);
				modules.add(module);
			}
			return this;
		}

		public Builder<T, U> setConfigClass(Class<T> clazz) {
			configurationClass = Optional.ofNullable(clazz);
			return this;
		}

		public GuiceBundle<T, U> build() {
            return new GuiceBundle<>(modules, configurationClass);
        }
	}

	private GuiceBundle(List<Module> modules, Optional<Class<T>> configurationClass) {
		Preconditions.checkNotNull(modules);
        Preconditions.checkArgument(!modules.isEmpty());
        this.modules = modules;
        this.configurationClass = configurationClass;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(Bootstrap<?, ?> bootstrap) {
		// adding config module first
		modules.add(new ConfigModule<>(bootstrap));
		if (configurationClass.isPresent()) {
			gjexEnvironmentModule = new GJEXEnvironmentModule<>(configurationClass.get());
		} else {
			gjexEnvironmentModule = new GJEXEnvironmentModule<>(GJEXConfiguration.class);
		}
		modules.add(gjexEnvironmentModule);
		// add Metrics MetricsInstrumentationModule
		modules.add(MetricsInstrumentationModule.builder().withMetricRegistry(bootstrap.getMetricRegistry()).build());
		// add the Validation module
		modules.add(new ImplicitValidationModule());
		// add the Api module before Tracing module so that APIs are timed from the start of execution
		modules.add(new ApiModule());
		// add the Tracing module before Task module so that even Concurrent tasks can be traced
		modules.add(new TracingModule());
		// add the Task module
		modules.add(new TaskModule());
		// add the Dashboard module
		modules.add(new DashboardModule(bootstrap));
		// add the Grpc Server module
		modules.add(new ServerModule());
		baseInjector = Guice.createInjector(this.modules);
	}

	@Override
	public void run(T configuration, U configMap, Environment environment) {
		setEnvironment(configuration, environment); // NOTE
		GrpcServer grpcServer = baseInjector.getInstance(GrpcServer.class);

		List<BindableService> bindableServices = new ArrayList<>();
		// Add all Grpc Services to the Grpc Server
		bindableServices.addAll(getInstances(baseInjector, BindableService.class));

		// Add all Grpc Health Check Services to the Grpc Server
		bindableServices.addAll(getInstances(baseInjector, HealthGrpc.HealthImplBase.class));

		grpcServer.registerServices(bindableServices);

		// Add all Grpc Filters to the Grpc Server
		filters = getInstances(baseInjector, Filter.class);
		grpcServer.registerFilters(filters, bindableServices);

		// Add all Grpc Filters to the Grpc Server
		tracingSamplers = getInstances(baseInjector, TracingSampler.class);
		grpcServer.registerTracingSamplers(tracingSamplers, bindableServices);

		// Register all ResponseMetered methods to publish metrics
		grpcServer.registerResponseMeteredMethods(bindableServices);

		ScheduledJobManager scheduledJobManager = baseInjector.getInstance(ScheduledJobManager.class);
		// Add all ScheduledJobs to the ScheduleJobManager
		scheduledJobs = getInstances(baseInjector, ScheduledJob.class);
		scheduledJobManager.registerScheduledJobs(scheduledJobs);

		// Lookup all Service implementations
		services = getInstances(baseInjector, Service.class);
		// Lookup all HealthCheck implementations
		healthchecks = getInstances(baseInjector, HealthCheck.class);
		
		ApiServer apiServer = baseInjector.getInstance(ApiServer.class);
		// Add all custom web resources
		resourceConfigs = getInstances(baseInjector, ResourceConfig.class);
		apiServer.registerResources(resourceConfigs);		
	}

	@SuppressWarnings("unchecked")
	private void setEnvironment(final T configuration, final Environment environment) {
		gjexEnvironmentModule.setEnvironmentData(configuration, environment);
	}

	@Override
	public List<Service> getServices() {		
        Preconditions.checkState(baseInjector != null,
                "Service(s) are only available after GuiceBundle.run() is called");
		return this.services;
	} 

	@Override
	public List<Filter> getFilters() {		
        Preconditions.checkState(baseInjector != null,
                "Filter(s) are only available after GuiceBundle.run() is called");
		return this.filters;
	} 
	
	@Override
	public List<HealthCheck> getHealthChecks() {		
        Preconditions.checkState(baseInjector != null,
                "HealthCheck(s) are only available after GuiceBundle.run() is called");
		return this.healthchecks;
	} 

	@Override
	public List<TracingSampler> getTracingSamplers() {
        Preconditions.checkState(baseInjector != null,
                "TracingSampler(s) is only available after GuiceBundle.run() is called");
        return this.tracingSamplers;
	}

	@Override
	public List<ScheduledJob> getScheduledJobs() {
		Preconditions.checkState(baseInjector != null,
				"ScheduledJob(s) are only available after GuiceBundle.run() is called");
		return this.scheduledJobs;
	}

	@Override
	public List<ResourceConfig> getResourceConfigs() {
        Preconditions.checkState(baseInjector != null,
                "ResourceConfig(s) is only available after GuiceBundle.run() is called");
        return this.resourceConfigs;
	}
	
	public Injector getInjector() {
        Preconditions.checkState(baseInjector != null,
                "Injector is only available after GuiceBundle.initialize() is called");
        return baseInjector;
    }	
		
    private <S> List<S> getInstances(Injector injector, Class<S> type) {
        List<S> instances = new ArrayList<S>();
        List<Binding<S>> bindings = injector.findBindingsByType(TypeLiteral.get(type));
        for(Binding<S> binding : bindings) {
            Key<S> key = binding.getKey();
            instances.add(injector.getInstance(key));
        }
        return instances;
    }

}
