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
    
    /**
     *  Root for dashboard webapp configs.
     */
    String DASHBOARD = "dashboard";

    /** Useful constants for servlet container configuration parts */
    String DASHBOARD_CONTEXT_PATH = "/admin";

}
