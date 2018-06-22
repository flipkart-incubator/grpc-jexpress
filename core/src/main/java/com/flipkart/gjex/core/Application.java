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
package com.flipkart.gjex.core;

import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.setup.Bootstrap;
import com.flipkart.gjex.core.setup.Environment;

/**
 * The base class for a GJEX application
 * 
 * @author regu.b
 *
 */
public abstract class Application implements Logging {
	
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
	public abstract void initialize(Bootstrap bootstrap);
	
	/**
	 * Runs this Application in the specified Environment
	 * @param environment the Environment to run in
	 * @throws Exception in case of errors during run
	 */
	public abstract void run(Environment environment) throws Exception;
	
	/**
	 * Parses command-line arguments and runs this Application. Usually called from a {@code public
     * static void main} entry point
     * 
	 * @param arguments command-line arguments for starting this Application
	 * @throws Exception in case of errors during run
	 */
	public final void run(String[] arguments) throws Exception {
		final Bootstrap bootstrap = new Bootstrap(this);
	}
	
}
