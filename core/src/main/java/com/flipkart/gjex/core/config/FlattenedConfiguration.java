package com.flipkart.gjex.core.config;

import org.apache.commons.configuration.AbstractConfiguration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FlattenedConfiguration extends AbstractConfiguration {

    private final Map<String, Object> flattenedConfig;

    public FlattenedConfiguration(Map<String, Object> flattenedMap) {
        this.flattenedConfig = new HashMap<>(flattenedMap);
    }

    @Override
    protected void addPropertyDirect(String key, Object value) {
    }

    @Override
    public boolean isEmpty() {
        return flattenedConfig.isEmpty();
    }

    @Override
    public boolean containsKey(String key) {
        return flattenedConfig.containsKey(key);
    }

    @Override
    public Object getProperty(String key) {
        return flattenedConfig.get(key);
    }

    @Override
    public Iterator<String> getKeys() {
        return flattenedConfig.keySet().iterator();
    }

}
