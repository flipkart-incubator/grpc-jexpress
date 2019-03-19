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
package com.flipkart.gjex.guice.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.config.*;
import com.flipkart.gjex.core.setup.Bootstrap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.name.Names;
import javafx.util.Pair;
import org.apache.commons.configuration.Configuration;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.Validator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is the module that fetches configuration from provided source (either yml or some external service).
 * It implicitly assumes that if you are using some external service for fetching configuration (example config service,
 * see ConfigServiceBundle.java in contrib folder for example implementation), then that bundle must be added before GuiceBundle in your
 * respective Application class.
 */
public class ConfigModule<T extends GJEXConfiguration, U extends Map> extends AbstractModule {

    private final Bootstrap<T, U> bootstrap;

    private final T configuration;
    private final U configMap;

    public ConfigModule(Bootstrap<T, U> _bootstrap) {
        this.bootstrap = _bootstrap;
        try {
            Pair<T, U> pair = parseConfiguration(bootstrap.getConfigurationFactoryFactory(), bootstrap.getConfigurationSourceProvider(),
                    bootstrap.getValidatorFactory().getValidator(), bootstrap.getConfigPath(), bootstrap.getConfigurationClass(),
                    bootstrap.getObjectMapper());
            configuration = pair.getKey();
            configMap = pair.getValue();
            this.bootstrap.setConfiguration(configuration); // NOTE
            this.bootstrap.setConfigMap(configMap); // NOTE
        } catch (Exception e) {
            throw new RuntimeException("Error while reading/parsing configuration. ", e);
        }
    }

    private Pair<T, U> parseConfiguration(ConfigurationFactoryFactory<T, U> configurationFactoryFactory,
                                          ConfigurationSourceProvider provider,
                                          Validator validator,
                                          String configPath,
                                          Class<T> klass,
                                          ObjectMapper objectMapper) throws IOException, ConfigurationException {
        final ConfigurationFactory<T, U> configurationFactory = configurationFactoryFactory
                .create(klass, validator, objectMapper);
        if (configPath != null) {
            return configurationFactory.build(provider, configPath);
        }
        return configurationFactory.build();
    }


    @Override
    protected void configure() {
        // bind config map instance
        bind(Map.class).annotatedWith(Names.named("GlobalMapConfig")).toInstance(configMap);

        // Flatten map and create named annotations for Flattened keys
        Map<String, Object> flattenedMap = new HashMap<>();
        flatten(flattenedMap, null, configMap);
        for (Map.Entry<String, Object> entry: flattenedMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            LinkedBindingBuilder annotatedWith = bind(value.getClass()).annotatedWith(Names.named(key));
            annotatedWith.toInstance(value);
        }
    }

    private void flatten(Map<String, Object> result, String prefix, Map<?, ?> map) {
        Set<?> keys = map.keySet();
        for(Object key : keys) {
            String name = (prefix != null) ? (prefix + "." + key.toString()) : key.toString();
            Object value = map.get(key);
            if(value instanceof Map) {
                flatten(result, name, (Map<?, ?>) value);
            }
            else {
                result.put(name, value);
            }
        }
    }

    /**
     * Returns the Global config of all flattened out properties loaded by instance of this class.
     */
    @Named("GlobalFlattenedConfig")
    @Provides
    @Singleton
    public Configuration getGlobalConfiguration(@Named("GlobalMapConfig") Map globalMapConfig) {
        Map<String, Object> flattenedMap = new HashMap<>();
        flatten(flattenedMap, null, globalMapConfig);
        Configuration configuration = new FlattenedConfiguration(flattenedMap);
        return configuration;
    }
}
