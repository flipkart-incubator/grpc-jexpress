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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.gjex.core.config.*;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.setup.Bootstrap;
import com.flipkart.gjex.core.setup.Environment;
import javafx.util.Pair;
import net.sourceforge.argparse4j.inf.Namespace;

import javax.validation.Validator;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Map;

/**
 * The base class for a GJEX application
 * 
 * @author regu.b
 *
 */
public abstract class Application<T extends GJEXConfiguration, U extends Map> implements Logging {
	
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

	private final ArgumentParserWrapper argumentParser;
    /**
     * Constructor for this class
     */
    public Application() {
    	this.argumentParser = new ArgumentParserWrapper();
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
	public abstract void initialize(Bootstrap<T, U> bootstrap);

	/**
	 * When the application runs, this is called after the {@link Bundle}s are run. Override it to add
	 * providers, resources, etc. for your application.
	 *
	 * @param configuration the parsed {@link GJEXConfiguration} object
	 * @param environment   the application's {@link Environment}
	 * @throws Exception if something goes wrong
	 */
	public abstract void run(T configuration, U configMap, Environment environment) throws Exception;
	
	/**
	 * Parses command-line arguments and runs this Application. Usually called from a {@code public
     * static void main} entry point
     * 
	 * @param arguments command-line arguments for starting this Application
	 * @throws Exception in case of errors during run
	 */
	public final void run(String... arguments) throws Exception {
		info("** GJEX starting up... **");
		long start = System.currentTimeMillis();

		final Bootstrap<T, U> bootstrap = new Bootstrap<>(this);
		/* Hook for applications to initialize their pre-start environment using bootstrap's properties */
        initialize(bootstrap);

        /* Create Environment */
        Environment environment = new Environment(getName(), bootstrap.getMetricRegistry());

		Namespace namespace = argumentParser.parseArguments(arguments);
		String configFilePath = namespace.getString("file");
		Pair<T, U> pair = parseConfiguration(bootstrap.getConfigurationFactoryFactory(), bootstrap.getConfigurationSourceProvider(),
				bootstrap.getValidatorFactory().getValidator(), configFilePath, getConfigurationClass(), bootstrap.getObjectMapper());
		T configuration = pair.getKey();
		U configMap = pair.getValue();

		/* Run bundles etc */
		bootstrap.run(configuration, configMap, environment);

        /* Run this Application */        
        run(configuration, configMap, environment);

	    final Object[] displayArgs = {this.getName(), (System.currentTimeMillis() - start), hostName};
		info(STARTUP_DISPLAY.format(displayArgs));
	    info("** GJEX startup complete **");
	    
	}
	public Class<T> getConfigurationClass() {
		return Generics.getTypeParameter(getClass(), GJEXConfiguration.class);
	}

	private Pair<T, U> parseConfiguration(ConfigurationFactoryFactory<T, U> configurationFactoryFactory,
								 ConfigurationSourceProvider provider,
								 Validator validator,
								 String configPath,
								 Class<T> klass,
								 ObjectMapper objectMapper) throws IOException, ConfigurationException {
		final ConfigurationFactory<T, U> configurationFactory = configurationFactoryFactory
				.create(klass, validator, objectMapper);
		if (configPath != null) {
			return configurationFactory.build(provider, configPath);
		}
		return configurationFactory.build();
	}
}
