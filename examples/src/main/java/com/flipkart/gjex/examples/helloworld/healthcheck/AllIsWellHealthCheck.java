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
package com.flipkart.gjex.examples.helloworld.healthcheck;

import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.task.RotationManagementTask;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.dropwizard.metrics5.health.HealthCheck;

/**
 * A HealthCheck implementation that reports positive results always
 * @author regu.b
 *
 */
@Singleton
public class AllIsWellHealthCheck extends HealthCheck implements Logging {

	private RotationManagementTask rotationManagementTask;

	@Inject
	public AllIsWellHealthCheck(RotationManagementTask rotationManagementTask) {
		this.rotationManagementTask = rotationManagementTask;
	}

	@Override
	protected Result check() {
		if (rotationManagementTask.isBir()) {
			info("Returning healthy status.");
			return Result.healthy("Server is " + rotationManagementTask.getStatus());
		} else {
			info("Returning unhealthy status.");
			return Result.unhealthy("Server is " + rotationManagementTask.getStatus());
		}
	}

	public String getStatus() {
		return rotationManagementTask.getStatus();
	}

	public String makeOor() {
		return rotationManagementTask.makeOor();
	}

	public String makeBir() {
		return rotationManagementTask.makeBir();
	}

	public boolean isBir() {
		return rotationManagementTask.isBir();
	}
}
