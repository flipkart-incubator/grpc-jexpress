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
package com.flipkart.grpc.jexpress.healthcheck;

import io.dropwizard.metrics5.health.HealthCheck;
import com.flipkart.gjex.core.logging.Logging;

public class AllIsWellHealthCheck extends HealthCheck implements Logging {

	@Override
	protected Result check() throws Exception {
		info("Returning healthy status.");
		return Result.healthy("All Is Well");
	}

}
