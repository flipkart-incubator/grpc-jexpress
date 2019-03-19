package com.flipkart.gjex.guice.module;

import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.setup.Environment;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;

import java.util.Map;

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
