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

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.MessageFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.gjex.Constants;
import com.flipkart.gjex.core.config.*;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.parser.ArgumentParserWrapper;
import com.flipkart.gjex.core.setup.Bootstrap;
import com.flipkart.gjex.core.setup.Environment;
import com.google.common.base.Strings;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * The base class for a GJEX application
 * 
 * @author regu.b
 *
 */
public abstract class Application<T extends JExpressConfiguration> implements Logging {
	
	/** The GJEX startup display contents*/
	private static final MessageFormat STARTUP_DISPLAY = new MessageFormat(
			"\n*************************************************************************\n" +
					" ╔═╗ ╦╔═╗═╗ ╦  " + "    Application name : {0} \n" +
					" ║ ╦ ║║╣ ╔╩╦╝  " + "    Startup Time : {1}" + " ms\n" +
					" ╚═╝╚╝╚═╝╩ ╚═  " + "    Host Name: {2} \n " +
					"*************************************************************************"
    );
    
	/** The machine name where this GJEX instance is running */
	private String hostName;
	private final ArgumentParserWrapper parser;

	public final Class<T> getConfigurationClass() {
		return Generics.getTypeParameter(getClass(), JExpressConfiguration.class);
	}
	
	/*
	
    /**
     * Constructor for this class
     */
    public Application() {
			this.parser = new ArgumentParserWrapper();
	    	try {
	    		this.hostName = InetAddress.getLocalHost().getHostName();
	    	} catch (UnknownHostException e) {
	    		//ignore the exception, not critical information
	    	}        
    }
	
	/**
	 * Gets the name of this GJEX application
	 * @return
	 */
	public String getName() {
        return getClass().getSimpleName();
    }
	
	/**
	 * Initializes this Application using the Bootstrap provided. Derived types may perform startup/one-time initializations 
	 * by implementing this method.
	 * @param bootstrap the Bootstrap for this Application
	 */
	public abstract void initialize(T configuration, Bootstrap<T> bootstrap);
	
	/**
	 * Runs this Application in the specified Environment
	 * @param environment the Environment to run in
	 * @throws Exception in case of errors during run
	 */
	public abstract void run(T configuration, Environment environment) throws Exception;
	
	/**
	 * Parses command-line arguments and runs this Application. Usually called from a {@code public
     * static void main} entry point
     * 
	 * @param arguments command-line arguments for starting this Application
	 * @throws Exception in case of errors during run
	 */
	public final void run(String ... arguments) throws Exception {
		info("** GJEX starting up... **");
		long start = System.currentTimeMillis();
		
		final Bootstrap bootstrap = new Bootstrap(this);



        /* Create Environment */
        Environment environment = new Environment(getName(),bootstrap.getMetricRegistry());      

		YamlConfigurationFactory<T> factory = new YamlConfigurationFactory<T>(getConfigurationClass(), new ObjectMapper());

		T configuration = factory.build(new FileConfigurationSourceProvider(), getConfigurationFile(arguments));

		/* Hook for applications to initialize their pre-start environment using bootstrap's properties */
		initialize(configuration, bootstrap);

		/* Run bundles etc */
		bootstrap.run(configuration, environment);

        /* Run this Application */        
        run(configuration, environment);

	    final Object[] displayArgs = {
	    			this.getName(),
				(System.currentTimeMillis() - start),
				this.hostName,
	    };
		info(STARTUP_DISPLAY.format(displayArgs));
	    info("** GJEX startup complete **");
	    
	}

	private String getConfigurationFile(String ...arguments){
		final Namespace namespace;

		String configurationFile="";
		try {
			namespace = parser.parseArguments(arguments);
			configurationFile = namespace.getString("file");
		} catch (ArgumentParserException e) {
		}

		if(Strings.isNullOrEmpty(configurationFile)){
			configurationFile = System.getProperty(Constants.CONFIG_FILE_PROPERTY);
		}
		if(Strings.isNullOrEmpty(configurationFile)){
			URL resource = this.getClass().getClassLoader().getResource(Constants.CONFIGURATION_YML);
			if(resource != null) {
				configurationFile = resource.getFile();
			}
		}

		return configurationFile;
	}
}
