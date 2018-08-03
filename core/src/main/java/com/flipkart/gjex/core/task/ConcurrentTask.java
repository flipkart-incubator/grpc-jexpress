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

	/**
     * Defines the maximum time GJEX can "expect" the task to run for.
     * In case the task does not produce a result within the timeout, the task may be retried by GJEX (based on the number of retries remaining)
     * Any result produced by the task after the timeout value has elapsed will be ignored by the GJEX engine and
     * will not be used by or passed on to other tasks that may be dependent on these results.
     */
    long timeout();	
    
    /** 
     * The number of times GJEX can retry the task in case of runtime failures.
     * Note: Runtime failures are failures encountered by GJEX - such as a task being timed out, or GJEX not receiving the response on time.
     * If a task throws an exception, it means the task executed successfuly and passed a response to GJEX.
     */
    long retries() default 0;    
    
    /**
     * Indicates the maximum concurrency for this Task within a single GJEX JVM node.
     */
    int concurrency() default 10;
}
