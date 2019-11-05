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
package com.flipkart.gjex.core.tracing;

import java.util.HashMap;

import javax.inject.Named;
import javax.inject.Singleton;

import com.flipkart.gjex.core.GJEXError;
import com.flipkart.gjex.core.GJEXError.ErrorType;

/**
 * A convenience data holder class
 * @author regu.b
 */
@Singleton
@Named("TracingSamplerHolder")
public class TracingSamplerHolder extends HashMap<String, TracingSampler> {
	
	/** Name for this TracingSamplerHolder*/
	public static final String TRACING_SAMPLER_HOLDER_NAME = "GJEX_TracingSamplerHolder";
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Updates the tracing sampler for the component specified with the specified sampling rate
	 * @param serviceName the service name
	 * @param samplerComponentName the sampled component name
	 * @param samplerRate the rate of sampling
	 */
	public void updateTracingSampler(String serviceName, String samplerComponentName, float samplerRate) {
		ConfigurableTracingSampler tracingSampler = (ConfigurableTracingSampler)this.get(serviceName);
		if (tracingSampler == null) {
			throw new GJEXError(ErrorType.runtime, "No tracing configuration found for : " + samplerComponentName, null);
		}
		tracingSampler.updateSamplingRate(samplerComponentName, samplerRate);
	}
	
	
}
