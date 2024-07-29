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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.grpc.BindableService;

/**
 * Annotation for specifying that a method invocation needs to be traced as a Span in an active distributed trace.
 * @author regu.b
 *
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Traced {

	/** Convenience sampling constants */
	public static final float RATE_NEVER_SAMPLE = 0f;
	public static final float RATE_ALWAYS_SAMPLE = 1f;
	public static final boolean BOOLEAN_NEVER_SAMPLE = false;

	/**
     * Returns the sampling rate on a scale of 0.0 to 1.0, where 0.0 indicates no sampling and 1.0 indicates all requests are sampled
     * Default value is to sample always {@link Traced#RATE_ALWAYS_SAMPLE}
     * Note that this property is overriden by {@link #withTracingSampler()}, if any is specified
     */
	float withSamplingRate() default Traced.RATE_ALWAYS_SAMPLE;

	/**
	 * Tracing is delegated to the specified sampler. Note that specifying a TracingSampler overrides any {@link #withSamplingRate()} setting.
	 * NOTE : In GJEX, this attribute is interpreted and used only when specified for gRPC services i.e. sub-types of {@link BindableService}
	 * @return null or a TracingSampler implementation
	 */
	Class<? extends TracingSampler> withTracingSampler() default TracingSampler.class;

}
