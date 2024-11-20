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
package com.flipkart.gjex.core.healthcheck;

import io.dropwizard.metrics5.health.HealthCheck;

import java.util.SortedMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A sub-type of the io.dropwizard.metrics5.health.HealthCheckRegistry that runs health checks concurrently
 * @author regu.b
 *
 */
public class HealthCheckRegistry extends io.dropwizard.metrics5.health.HealthCheckRegistry {

	/** Name for this HealthCheckRegistry*/
	public static final String HEALTHCHECK_REGISTRY_NAME = "GJEX_HealthCheckRegistry";

  // Creating a cached threadpool as the number of HealthCheck instances are anyway unknown and
  // hence no point in bounding it to a number
  private final ExecutorService executorService = Executors.newCachedThreadPool(new NamedThreadFactory(
    "GJEX-healthcheck-"));

  /**
   * Runs HealthChecks concurrently using the ExecutorService
   */
  @Override
  public SortedMap<String, HealthCheck.Result> runHealthChecks() {
      return this.runHealthChecks(this.executorService);
  }

  private static class NamedThreadFactory implements ThreadFactory {

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    NamedThreadFactory(String namePrefix) {
      SecurityManager s = System.getSecurityManager();
      group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
      this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
      Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
      t.setDaemon(true);
      if (t.getPriority() != Thread.NORM_PRIORITY)
        t.setPriority(Thread.NORM_PRIORITY);
      return t;
    }
  }
}
