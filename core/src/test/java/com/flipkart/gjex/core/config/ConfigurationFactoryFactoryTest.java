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

    private final ConfigurationFactoryFactory<BaseConfigurationFactoryTest.ExampleConfig, Map> factoryFactory = new DefaultConfigurationFactoryFactory<>();
    private final Validator validator = null;

    @Test
    public void createDefaultFactory() throws Exception {
        File validFile = new File(Resources.getResource("factory-test-valid.yml").toURI());
        ConfigurationFactory<BaseConfigurationFactoryTest.ExampleConfig, Map> factory =
                factoryFactory.create(BaseConfigurationFactoryTest.ExampleConfig.class, validator,
                        GJEXObjectMapper.newObjectMapper());
        final BaseConfigurationFactoryTest.ExampleConfig example = factory.build(validFile).getKey();
        assertThat(example.getName()).isEqualTo("GJEX");
    }

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


    private static final class PassThroughConfigurationFactoryFactory
            extends DefaultConfigurationFactoryFactory<BaseConfigurationFactoryTest.ExampleConfig, Map> {
        @Override
        protected ObjectMapper configureObjectMapper(ObjectMapper objectMapper) {
            return objectMapper;
        }
    }
}