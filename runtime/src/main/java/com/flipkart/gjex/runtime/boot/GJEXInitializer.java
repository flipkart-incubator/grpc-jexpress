/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.flipkart.gjex.runtime.boot;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flipkart.gjex.Constants;
import com.flipkart.gjex.core.config.FileLocator;
import com.flipkart.gjex.core.config.YamlConfiguration;
import com.flipkart.gjex.core.service.Service;
import com.flipkart.gjex.guice.module.ConfigModule;
import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.yammer.metrics.guice.InstrumentationModule;

/**
 * <code>GJEXInitializer</code> initializes the grpc-jexpress (GJEX) runtime using the various Guice modules
 *
 * @author regunath.balasubramanian
 */
public class GJEXInitializer {

	/** Logger for this class */
    private static final Logger logger = LoggerFactory.getLogger(GJEXInitializer.class);
	
	/** The machine name where this GJEX instance is running */
	private String hostName;
	
	/** List of initialized Service instances*/
	List<Service> services;

    /**
     * Constructor for this class
     */
    public GJEXInitializer() {
	    	try {
	    		this.hostName = InetAddress.getLocalHost().getHostName();
	    	} catch (UnknownHostException e) {
	    		//ignore the exception, not critical information
	    	}        
    }
	
    private void loadGJEXRuntimeContainer() {
    		// get System configs 
    		final ConfigModule systemConfigModule = new ConfigModule();
    		// locate and load all GRPC modules
		List<AbstractModule> grpcModules = new LinkedList<AbstractModule>();
        try {
	    		for (File grpcModuleConfig : FileLocator.findFiles(Constants.GRPC_MODULE_NAMES_CONFIG)) {
	    			YamlConfiguration yamlConfiguration = new YamlConfiguration(grpcModuleConfig.toURI().toURL());
	    			Iterator<String> propertyKeys = yamlConfiguration.getKeys();
	    	        while (propertyKeys.hasNext()) {
	    	            String propertyKey = propertyKeys.next();
	    	            if (propertyKey.equalsIgnoreCase(Constants.GRPC_MODULE_NAMES)) {
	    	            		for (String moduleName : (String[])yamlConfiguration.getProperty(propertyKey)) {
	    	            			grpcModules.add((AbstractModule)Class.forName(moduleName).newInstance());
	    	            		}
	    	            }
	    	        }
	    		}
	    		// add the Metrics InstrumentationModule
	    		grpcModules.add(new InstrumentationModule());
        } catch (Exception e) {
        		logger.error("Error loading GJEX Runtime Container", e);
            throw new RuntimeException(e);
        }
		Injector injector = Guice.createInjector(grpcModules);
		// Identify all Service implementations, start them and register for Runtime shutdown hook
		this.services = this.getInstances(injector, Service.class);
		this.services.forEach(service -> {
			try {
				service.start();
			} catch (Exception e) {
				logger.error("Error starting a Service : " + service.getClass().getName(), e);
                throw new RuntimeException(e);
			}
		});
		this.registerServicesForShutdown();
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
    
    private void registerServicesForShutdown() {
    		Runtime.getRuntime().addShutdownHook(new Thread() {
    			@Override
    			public void run() {
	    	        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
	    	        System.err.println("*** Shutting down gRPC server since JVM is shutting down");
	    	        services.forEach(Service::stop);
	    	        System.err.println("*** Server shut down");
    			}
    	    });    		
    }
    
}
