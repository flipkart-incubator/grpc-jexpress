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

import com.codahale.metrics.MetricRegistry;
import com.flipkart.gjex.core.Bundle;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.service.Service;
import com.flipkart.gjex.core.setup.Bootstrap;
import com.flipkart.gjex.core.setup.Environment;
import com.flipkart.gjex.guice.module.ConfigModule;
import com.flipkart.gjex.guice.module.DashboardModule;
import com.flipkart.gjex.guice.module.ServerModule;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.palominolabs.metrics.guice.MetricsInstrumentationModule;

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
            return build();
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
		this.modules.add(MetricsInstrumentationModule.builder().withMetricRegistry(new MetricRegistry()).build());
		// add the Dashboard module
		this.modules.add(new DashboardModule());
		// add the Grpc Server module
		this.modules.add(new ServerModule());
		this.baseInjector = Guice.createInjector(this.modules);
	}

	@Override
	public void run(Environment environment) {
		this.services = this.getInstances(this.baseInjector, Service.class);
	}	

	@Override
	public List<Service> getServices() {		
		return this.services;
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
