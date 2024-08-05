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
import com.flipkart.gjex.core.GJEXObjectMapper;
import com.google.common.io.Resources;
import org.junit.Test;

import javax.validation.Validator;
import java.io.File;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ConfigurationFactoryFactoryTest {

    @SuppressWarnings("rawtypes")
    private final ConfigurationFactoryFactory<BaseConfigurationFactoryTest.ExampleConfig, Map> factoryFactory = new DefaultConfigurationFactoryFactory<>();
    private final Validator validator = null;

    @SuppressWarnings("rawtypes")
    @Test
    public void createDefaultFactory() throws Exception {
        File validFile = new File(Resources.getResource("factory-test-valid.yml").toURI());
        ConfigurationFactory<BaseConfigurationFactoryTest.ExampleConfig, Map> factory =
                factoryFactory.create(BaseConfigurationFactoryTest.ExampleConfig.class, validator,
                        GJEXObjectMapper.newObjectMapper());
        final BaseConfigurationFactoryTest.ExampleConfig example = factory.build(validFile).getKey();
        assertThat(example.getName()).isEqualTo("GJEX");
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void createDefaultFactoryFailsUnknownProperty() throws Exception {
        File validFileWithUnknownProp = new File(
                Resources.getResource("factory-test-unknown-property.yml").toURI());

        ConfigurationFactory<BaseConfigurationFactoryTest.ExampleConfig, Map> factory =
                factoryFactory.create(BaseConfigurationFactoryTest.ExampleConfig.class, validator,
                        GJEXObjectMapper.newObjectMapper());

        assertThatExceptionOfType(ConfigurationException.class)
                .isThrownBy(() -> factory.build(validFileWithUnknownProp))
                .withMessageContaining("Unrecognized field at: howIsTheJosh");
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void createFactoryAllowingUnknownProperties() throws Exception {
        ConfigurationFactoryFactory<BaseConfigurationFactoryTest.ExampleConfig, Map> customFactory = new PassThroughConfigurationFactoryFactory();

        File validFileWithUnknownProp = new File(
                Resources.getResource("factory-test-unknown-property.yml").toURI());

        ConfigurationFactory<BaseConfigurationFactoryTest.ExampleConfig, Map> factory =
                customFactory.create(
                        BaseConfigurationFactoryTest.ExampleConfig.class,
                        validator,
                        GJEXObjectMapper.newObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        BaseConfigurationFactoryTest.ExampleConfig example = factory.build(validFileWithUnknownProp).getKey();
        assertThat(example.getName()).isEqualTo("GJEX");
    }


    @SuppressWarnings("rawtypes")
    private static final class PassThroughConfigurationFactoryFactory
            extends DefaultConfigurationFactoryFactory<BaseConfigurationFactoryTest.ExampleConfig, Map> {
        @Override
        protected ObjectMapper configureObjectMapper(ObjectMapper objectMapper) {
            return objectMapper;
        }
    }
}
