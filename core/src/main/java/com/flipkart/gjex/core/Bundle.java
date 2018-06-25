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

import java.util.List;

import com.flipkart.gjex.core.service.Service;
import com.flipkart.gjex.core.setup.Bootstrap;
import com.flipkart.gjex.core.setup.Environment;

/**
 * A reusable bundle of functionality, used to define blocks of application behavior.
 */
public interface Bundle {
	
    /**
     * Initializes this Bundle with the application bootstrap.
     *
     * @param bootstrap the application bootstrap
     */
    void initialize(Bootstrap bootstrap);

    /**
     * Runs this Bundle in the application environment.
     *
     * @param environment the application environment
     */
    void run(Environment environment);
    
    /**
     * Returns Service instances loaded by this Bundle
     * @return List containing Service instances
     */
    List<Service> getServices();
}
