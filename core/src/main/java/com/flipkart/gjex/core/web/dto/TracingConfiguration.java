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
package com.flipkart.gjex.core.web.dto;

/**
 * Data transfer object (DTO) for modifying tracing configuration of traced services/components
 * @author regu.b
 *
 */
public class TracingConfiguration {

	private String apiName;
	private String componentName;
	private float samplingRate;

	public TracingConfiguration() {}
	public TracingConfiguration(String apiName, String componentName, float samplingRate) {
		this.apiName = apiName;
		this.componentName = componentName;
		this.samplingRate = samplingRate;
	}

	public String getApiName() {
		return apiName;
	}
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	public float getSamplingRate() {
		return samplingRate;
	}
	public void setSamplingRate(float samplingRate) {
		this.samplingRate = samplingRate;
	}
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	@Override
	public String toString() {
		return "TracingConfiguration - " + componentName + " : " + samplingRate;
	}

}
