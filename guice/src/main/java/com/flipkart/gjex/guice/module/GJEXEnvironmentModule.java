/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.flipkart.gjex.guice.module;

import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.setup.Environment;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;

import java.util.Map;

/**
 * This binds configuration class type to actual instance of Configuration class (generated from reading either yml file or some external config service)
 * @param <T>
 * @param <U>
 */
public class GJEXEnvironmentModule<T extends GJEXConfiguration, U extends Map> extends AbstractModule implements Logging {

    private static final String ILLEGAL_GJEX_MODULE_STATE = "The GJEX environment has not been set yet. " +
            "This is likely caused by trying to access GJEX environment during the bootstrap phase.";

    private T configuration;
    private Environment environment;
    private Class<? super T> configurationClass;

    public GJEXEnvironmentModule(Class<T> configurationClass) {
        this.configurationClass = configurationClass;
    }

    @Override
    // Called at time of GuiceBundle.initialize(bootstrap)
    protected void configure() {

        // bind configuration class to instance using Provider
        Provider<T> provider = new CustomConfigurationProvider();
        bind(configurationClass).toProvider(provider);
        if (configurationClass != GJEXConfiguration.class) {
            bind(GJEXConfiguration.class).toProvider(provider);
        }
    }

    /**
     *  This method is called from GuiceBundle.run() method to set actual generated Configuration instance
        (constructed using either yml file or other sources like config-service).
        This instance will be returned when we try to inject GJEXConfiguration or its subclass class
     */
    public void setEnvironmentData(T configuration, Environment environment) {
        this.configuration = configuration;
        this.environment = environment;
    }

    @Provides
    public Environment providesEnvironment() {
        if (environment == null) {
            throw new ProvisionException(ILLEGAL_GJEX_MODULE_STATE);
        }
        return environment;
    }

    private class CustomConfigurationProvider implements Provider<T> {

        @Override
        public T get() {
            if (configuration == null) {
                throw new ProvisionException(ILLEGAL_GJEX_MODULE_STATE);
            }
            return configuration;
        }
    }
}
