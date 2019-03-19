package com.flipkart.gjex.guice.module;

import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.config.FlattenedConfiguration;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.setup.Environment;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.name.Names;
import org.apache.commons.configuration.Configuration;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GJEXEnvironmentModule<T extends GJEXConfiguration, U extends Map> extends AbstractModule implements Logging {

    private static final String ILLEGAL_GJEX_MODULE_STATE = "The GJEX environment has not been set yet. " +
            "This is likely caused by trying to access GJEX environment during the bootstrap phase.";

    private T configuration;
    private U configMap;
    private Environment environment;
    private Class<? super T> configurationClass;
    private Class<? super U> configMapClass;

    public GJEXEnvironmentModule(Class<T> configurationClass, Class<U> configMapClass) {
        this.configurationClass = configurationClass;
        this.configMapClass = configMapClass;
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

        // bind config map using Provider
        Provider<U> configMapProvider = new CustomConfigMapProvider();
        bind(configMapClass).annotatedWith(Names.named("GlobalMapConfig")).toProvider(configMapProvider);
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

    private void flatten(Map<String, Object> result, String prefix, Map<?, ?> dom) {
        Set<?> keys = dom.keySet();
        for(Object key : keys) {
            String name = (prefix != null) ? (prefix + "." + key.toString()) : key.toString();
            Object value = dom.get(key);
            if(value instanceof Map) {
                flatten(result, name, (Map<?, ?>) value);
            }
            else {
                result.put(name, value);
            }
        }
    }

    /**
     *  This method is called from GuiceBundle.run() method to set actual generated Configuration and configMap instances
        (constructed using either yml file or other sources like config-service).
        These instances are the one that actually get returned when we try to inject GJEXConfiguration or its class and
        flattened json config as map.
     */
    public void setEnvironmentData(T configuration, U configMap, Environment environment) {
        this.configuration = configuration;
        this.environment = environment;
        this.configMap = configMap;
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

    private class CustomConfigMapProvider implements Provider<U> {

        @Override
        public U get() {
            if (configMap == null) {
                throw new ProvisionException(ILLEGAL_GJEX_MODULE_STATE);
            }
            return configMap;
        }
    }
}
