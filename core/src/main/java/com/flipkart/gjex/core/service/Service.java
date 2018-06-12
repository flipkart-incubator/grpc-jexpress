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

package com.flipkart.gjex.core.service;

/**
 * <code>Service</code> defines an interface for Service-like components that have explicit Start and Stop lifecycles.
 * These methods can be invoked suitably by the container. Examples of implementation include JDBC Datasource, Http Connection pool etc.
 *
 * @author regunath.balasubramanian
 */

public interface Service {
	
	/**
	 * Starts the service. This method blocks until the service has completely started.
	*/
	void start() throws Exception;

	/**
	 * Stops the service. This method blocks until the service has completely shut down.
	 */
	void stop();
}
