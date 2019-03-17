package com.flipkart.gjex.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.gjex.core.GJEXConfiguration;

import javax.validation.Validator;
import java.util.Map;

public interface ConfigurationFactoryFactory<T extends GJEXConfiguration, U extends Map> {

    ConfigurationFactory<T, U> create(Class<T> klass,
                                      Validator validator,
                                      ObjectMapper objectMapper);
}
