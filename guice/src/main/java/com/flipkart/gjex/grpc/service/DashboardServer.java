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
package com.flipkart.gjex.grpc.service;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flipkart.gjex.core.service.Service;

/**
 * <code>DashboardServer</code> is a {@link Service} implementation that manages the GJEX Dashboard Jetty Server instance lifecycle
 * 
 * @author regunath.balasubramanian
 */

@Singleton
public class DashboardServer implements Service {

	/** Logger for this class*/
	private static final Logger LOGGER = LoggerFactory.getLogger(DashboardServer.class);
	
    private final Server dashboardServer;

	@Inject
	public DashboardServer(@Named("DashboardJettyServer") Server dashboardServer) {
		this.dashboardServer = dashboardServer;
	}
	
	@Override
	public void start() throws Exception {
		this.dashboardServer.start();
		LOGGER.info("Dashboard Server started and listening on port : " + this.dashboardServer.getURI().getPort());
	}

	@Override
	public void stop() {
		try {
			this.dashboardServer.stop();
		} catch (Exception e) {
			// Just log the error as we are stopping anyway
			LOGGER.error("Error stopping Dashboard server : " + e.getMessage(), e);
		}
	}

}
