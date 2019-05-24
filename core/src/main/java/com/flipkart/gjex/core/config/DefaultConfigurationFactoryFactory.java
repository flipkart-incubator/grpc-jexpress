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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.gjex.core.GJEXConfiguration;

import javax.validation.Validator;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class DefaultConfigurationFactoryFactory<T extends GJEXConfiguration, U extends Map> implements ConfigurationFactoryFactory<T, U> {

    @Override
    public ConfigurationFactory<T, U> create(Class<T> klass, Validator validator, ObjectMapper objectMapper) {
        return new YamlConfigurationFactory<>(klass, validator, configureObjectMapper(objectMapper.copy()));
    }

    /**
     * Provides additional configuration for the {@link ObjectMapper} used to read
     * the configuration. By default {@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES}
     * is enabled to protect against misconfiguration.
     *
     * @param objectMapper template to be configured
     * @return configured object objectMapper
     */
    protected ObjectMapper configureObjectMapper(ObjectMapper objectMapper) {
        return objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

}
