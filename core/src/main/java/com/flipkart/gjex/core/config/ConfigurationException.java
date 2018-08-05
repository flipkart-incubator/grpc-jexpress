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

package com.flipkart.gjex.core.config;

import com.flipkart.gjex.core.GJEXError;

/**
 * The <code>ConfigurationException</code> is sub-type of the {@link GJEXError} for use in the configuration modules  
 * 
 * @author regunath.balasubramanian
 */
public class ConfigurationException extends GJEXError {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor for ConfigurationException.
	 * @param msg the detail message
	 */
	public ConfigurationException(String msg) {
		super(GJEXError.ErrorType.runtime, msg, null);
	}

	/**
	 * Constructor for ConfigurationException.
	 * @param msg the detail message
	 * @param cause the root cause 
	 */
	public ConfigurationException(String msg, Throwable cause) {
		super(GJEXError.ErrorType.runtime, msg, cause);
	}
	
}