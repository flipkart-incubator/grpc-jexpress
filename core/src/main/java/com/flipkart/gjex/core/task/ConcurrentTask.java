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
package com.flipkart.gjex.core.task;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying an execution task in GJEX that executes concurrently and typically invokes a network resource, external gRPC service etc.
 * @author regu.b
 *
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConcurrentTask {

    /** Possible completion values*/
    public enum Completion {
        Mandatory, Optional;
    }

    /**
    * Indicates whether the successful completion of this ConcurrentTask is Mandatory or Optional when composing any final responses
    * from multiple ConcurrentTask executions
    */
    Completion completion() default Completion.Mandatory;

    /**
    * Defines the maximum time GJEX can "expect" the task to run for.
    * In case the task does not produce a result within the timeout, the task may be retried by GJEX (based on the number of retries remaining)
    * Any result produced by the task after the timeout value has elapsed will be ignored by the GJEX engine and
    * will not be used by or passed on to other tasks that may be dependent on these results.
    */
    int timeout() default 0;

    /**
    * Timeout configured as a Config property. Note that {@link ConcurrentTask#timeout()} overrides this value
    */
    String timeoutConfig() default "";

    /**
    * Indicates if requests may be hedged within the specified timeout duration.
    */
    boolean withRequestHedging() default false;

    /**
    * Indicates the maximum concurrency for this Task within a single GJEX JVM node.
    */
    int concurrency() default 10;

    /**
    * Concurrency configured as a Config property. Note that {@link ConcurrentTask#concurrency()} overrides this value
    */
    String concurrencyConfig() default "";

}
