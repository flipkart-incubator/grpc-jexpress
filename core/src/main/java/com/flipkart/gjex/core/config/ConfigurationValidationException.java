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
package com.flipkart.gjex.core.config;

import com.google.common.collect.ImmutableSet;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * An exception thrown where there is an error validating a configuration object.
 */
public class ConfigurationValidationException extends ConfigurationException {

    private final ImmutableSet<ConstraintViolation<?>> constraintViolations;

    /**
     * Creates a new ConfigurationException for the given path with the given errors.
     *
     * @param path      the bad configuration path
     * @param errors    the errors in the path
     */
    public <T> ConfigurationValidationException(String path, Set<ConstraintViolation<T>> errors) {
        super(path, ConstraintViolations.format(errors));
        this.constraintViolations = ConstraintViolations.copyOf(errors);
    }

    /**
     * Returns the set of constraint violations in the configuration.
     *
     * @return the set of constraint violations
     */
    public ImmutableSet<ConstraintViolation<?>> getConstraintViolations() {
        return constraintViolations;
    }
}
