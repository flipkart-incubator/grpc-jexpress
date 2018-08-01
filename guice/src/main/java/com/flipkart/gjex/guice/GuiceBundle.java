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
package com.flipkart.gjex.guice;

import java.util.ArrayList;
import java.util.List;

import com.codahale.metrics.health.HealthCheck;
import com.flipkart.gjex.core.Bundle;
import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.service.Service;
import com.flipkart.gjex.core.setup.Bootstrap;
import com.flipkart.gjex.core.setup.Environment;
import com.flipkart.gjex.grpc.service.GrpcServer;
import com.flipkart.gjex.guice.module.ConfigModule;
import com.flipkart.gjex.guice.module.DashboardModule;
import com.flipkart.gjex.guice.module.ServerModule;
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
public class GuiceBundle implements Bundle, Logging {

	private final List<Module> modules;
	private Injector baseInjector;
	private List<Service> services;
	@SuppressWarnings("rawtypes")
	private List<Filter> filters;
	private List<HealthCheck> healthchecks;
	
	public static class Builder {
		private List<Module> modules = Lists.newArrayList();
		public Builder addModules(Module... moreModules) {
			for (Module module : moreModules) {
				Preconditions.checkNotNull(module);
				modules.add(module);
			}
			return this;
		}
		public GuiceBundle build() {
            return new GuiceBundle(this.modules);
        }
	}
	public static Builder newBuilder() {
        return new Builder();
    }		
	
	private GuiceBundle(List<Module> modules) {
		Preconditions.checkNotNull(modules);
        Preconditions.checkArgument(!modules.isEmpty());
        this.modules = modules;
	}
	
	@Override
	public void initialize(Bootstrap bootstrap) {
		// add the Config and Metrics MetricsInstrumentationModule
		this.modules.add( new ConfigModule());
		this.modules.add(MetricsInstrumentationModule.builder().withMetricRegistry(bootstrap.getMetricRegistry()).build());
		// add the Validation module
		this.modules.add(new ImplicitValidationModule());
		// add the Tracing module
		this.modules.add(new TracingModule());
		// add the Dashboard module
		this.modules.add(new DashboardModule(bootstrap));
		// add the Grpc Server module
		this.modules.add(new ServerModule());
		this.baseInjector = Guice.createInjector(this.modules);
	}

	@Override
	public void run(Environment environment) {
		// Add all Grpc Services to the Grpc Server
		List<BindableService> services = this.getInstances(this.baseInjector, BindableService.class);

		this.baseInjector.getInstance(GrpcServer.class).registerServices(services);
		// Add all Grpc Filters to the Grpc Server
		this.filters = this.getInstances(this.baseInjector, Filter.class);
		this.baseInjector.getInstance(GrpcServer.class).registerFilters(this.filters, services);

		// Lookup all Service implementations
		this.services = this.getInstances(this.baseInjector, Service.class);
		// Lookup all HealthCheck implementations
		this.healthchecks = this.getInstances(this.baseInjector, HealthCheck.class);		
	}	

	@Override
	public List<Service> getServices() {		
        Preconditions.checkState(baseInjector != null,
                "Service(s) are only available after GuiceBundle.run() is called");
		return this.services;
	} 

	@SuppressWarnings("rawtypes")
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
	
	public Injector getInjector() {
        Preconditions.checkState(baseInjector != null,
                "Injector is only available after GuiceBundle.initialize() is called");
        return baseInjector;
    }	
	
    private <T> List<T> getInstances(Injector injector, Class<T> type) {
        List<T> instances = new ArrayList<T>();
        List<Binding<T>> bindings = injector.findBindingsByType(TypeLiteral.get(type));
        for(Binding<T> binding : bindings) {
            Key<T> key = binding.getKey();
            instances.add(injector.getInstance(key));
        }
        return instances;
    }

}
