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
