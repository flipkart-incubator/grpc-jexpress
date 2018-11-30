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
package com.flipkart.gjex;

/**
 * <code>Constants</code> maintains constant variables which would be used across GJEX modules.
 * 
 *  @author regunath.balasubramanian
 */
public interface Constants {
	
	String LOGGING_ID = "loggingId";

	String CONFIG_FILE_PROPERTY = "gjex.configurationFile";
	
    /**
     * Root for all packaged configs.
     */
    String CONFIG_ROOT = "packaged";

    /**
     * Configuration yml resource path.
     */
    String CONFIGURATION_YML = CONFIG_ROOT + "/configuration.yml";

    /**
     * Config file name for GRPC Service module class names
     */
    String GRPC_MODULE_NAMES_CONFIG = "grpc_modules.yml";
    
    /**
     * The GRPC Service modules property name
     */
    String GRPC_MODULE_NAMES = "GJEX.grpc.module.names";

    String GRPC_SERVER_PORT ="Grpc.server.port";

    String DASHBOARD_SERVER_PORT="Dashboard.service.port";

    String API_SCHEDULEDEXECUTOR_THREADPOOL_SIZE="Api.scheduledexecutor.threadpool.size";

    String DASHBOARD_SERVICE_ACCEPTORS="Dashboard.service.acceptors";

    String DASHBOARD_SERVICE_SELECTORS= "Dashboard.service.selectors";

    String DASHBOARD_SERVICE_WORKERS="Dashboard.service.workers";

    String API_SERVICE_PORT="Api.service.port";

    String API_SERVICE_ACCEPTORS="Api.service.acceptors";

    String API_SERVICE_WORKERS="Api.service.workers";

    String API_SERVICE_SELECTORS="Api.service.selectors";

    String GLOBALCONFIG="GlobalConfig";

}
