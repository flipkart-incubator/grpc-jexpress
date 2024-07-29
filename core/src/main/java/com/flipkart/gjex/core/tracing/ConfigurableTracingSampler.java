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
package com.flipkart.gjex.core.tracing;

import java.util.HashMap;
import java.util.Map;

import com.flipkart.gjex.core.logging.Logging;

/**
 * An implementation of {@link TracingSampler} that maintains component specific configuration overrides for tracing and for enabled
 * traces, a {@link CountingSampler} per component that determines if the call must be traced
 *
 * @author regu.b
 *
 */
public class ConfigurableTracingSampler implements TracingSampler, Logging {

	/** Map of components and their respective samplers */
	private Map<String, CountingSampler> componentMap = new HashMap<String, CountingSampler>();

	/**
	 * Interface method implementation.
	 * @see com.flipkart.gjex.core.tracing.TracingSampler#isSampled(java.lang.String)
	 */
	@Override
	public boolean isSampled(String component) {
		boolean isSampled = false;
		CountingSampler sampler = this.componentMap.get(component);
		if (sampler != null) {
			return sampler.isSampled();
		}
		return isSampled;
	}

	/**
	 * Interface method implementation.Initializes a {@link CountingSampler} for the specified component with the specified
	 * sampling rate
	 * @param component the component/method endpoint/URI path
	 * @param rate the sampling rate
	 */
	@Override
	public void initializeSamplerFor(String component, float rate) {
		CountingSampler sampler = this.componentMap.get(component);
		if (sampler == null) {
			sampler = new CountingSampler(rate);
			this.componentMap.put(component, sampler);
		}
	}

	/**
	 * Updates/Replaces the CountingSampler for the specified component with the new sampling rate
	 * @param component the component identifier
	 * @param rate the sampling rate
	 */
	public void updateSamplingRate(String component, float rate) {
		this.componentMap.put(component, new CountingSampler(rate));
	}

}
