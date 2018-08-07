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
	
	/** Flag to determine if tracing is enabled - this would ideally be controlled via a dynamic configuration system*/
	private boolean isTraceEnabled = false;

	/** Map of components and their respective samplers */
	private Map<String, CountingSampler> componentMap = new HashMap<String, CountingSampler>();
	
	/**
	 * Interface method implementation. 
	 * @see com.flipkart.gjex.core.tracing.TracingSampler#isSampled(java.lang.String)
	 */
	@Override
	public boolean isSampled(String component) {
		boolean isSampled = this.isTraceEnabled(component); // check for the config override
		if (!isSampled) {
			return false;
		}
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
	 * Override for enabling/disabling tracing for the component, say using configuration that reflects dynamically during runtime
	 * e.g. through a config push 
	 * Default behavior is to turn off tracing
	 */
	protected boolean isTraceEnabled(String component) {
		return this.isTraceEnabled;
	}
	
	/**
	 * Sets the trace enabled flag for this sampler. Resets the active samplers if tracing gets disabled.
	 * @param traceEnabled true or false to set/unset tracing
	 */
	protected void setIsTraceEnabled(boolean traceEnabled) {
		if (this.isTraceEnabled && !traceEnabled) {
			this.resetSamplers();
		}
		this.isTraceEnabled = traceEnabled;
	}
	
	/**
	 * Resets all active samplers
	 */
	protected void resetSamplers() {
		this.componentMap.clear();
	}	
	
}
