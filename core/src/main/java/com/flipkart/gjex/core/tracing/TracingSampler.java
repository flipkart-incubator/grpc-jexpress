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

/**
 * Interface to permit sampling of Traces. Implementations determine sampling based on static configuration or dynamic switches to
 * enable/disable tracing.
 *
 * @author regu.b
 *
 */
public interface TracingSampler {

	/**
	 * Determines if tracing must happen for the specified component
	 * Default is to never sample {@link Traced#BOOLEAN_NEVER_SAMPLE}
	 * @param component the name of the service or method endpoint where tracing originates
	 * @return boolean true or false
	 */
	default boolean isSampled(String component) {
		return Traced.BOOLEAN_NEVER_SAMPLE;
	}

	/**
	 * Registers for sampling of the component traces at the specified rate
	 * @param component the component identifier
	 * @param rate the sampling rate in the range 0.0f to 1.0f
	 */
	void initializeSamplerFor(String component, float rate);

}
