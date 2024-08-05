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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

import javax.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;

public class ConstraintViolations {
    private ConstraintViolations() { /* singleton */ }

    public static <T> String format(ConstraintViolation<T> v) {
        return String.format("%s %s", v.getPropertyPath(), v.getMessage());
    }

    public static <T> ImmutableList<String> format(Set<ConstraintViolation<T>> violations) {
        final Set<String> errors = new HashSet<>();
        for (ConstraintViolation<?> v : violations) {
            errors.add(format(v));
        }
        return ImmutableList.copyOf(Ordering.natural().sortedCopy(errors));
    }

    public static ImmutableList<String> formatUntyped(Set<ConstraintViolation<?>> violations) {
        final Set<String> errors = new HashSet<>();
        for (ConstraintViolation<?> v : violations) {
            errors.add(format(v));
        }
        return ImmutableList.copyOf(Ordering.natural().sortedCopy(errors));
    }

    public static <T> ImmutableSet<ConstraintViolation<?>> copyOf(Set<ConstraintViolation<T>> violations) {
        final ImmutableSet.Builder<ConstraintViolation<?>> builder = ImmutableSet.builder();
        for (ConstraintViolation<T> violation : violations) {
            builder.add(violation);
        }
        return builder.build();
    }
}
