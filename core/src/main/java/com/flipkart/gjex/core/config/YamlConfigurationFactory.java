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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.flipkart.gjex.core.GJEXConfiguration;

import javax.validation.Validator;
import java.util.Map;

/**
 * A factory class for loading YAML configuration files, binding them to configuration objects, and
 * validating their constraints.
 *
 */
@SuppressWarnings("rawtypes")
public class YamlConfigurationFactory<T extends GJEXConfiguration, U extends Map> extends BaseConfigurationFactory<T, U> {

    /**
    * Creates a new configuration factory for the given class.
    *
    * @param klass        the configuration class
    * @param validator    the validator to use
    * @param objectMapper the Jackson {@link ObjectMapper} to use
    */
    public YamlConfigurationFactory(Class<T> klass,
                                    Validator validator,
                                    ObjectMapper objectMapper) {
        super(new YAMLFactory(), YAMLFactory.FORMAT_NAME_YAML, klass, validator, objectMapper);
    }
}
