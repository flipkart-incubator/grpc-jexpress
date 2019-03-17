package com.flipkart.gjex.core.config;

import org.apache.commons.configuration.AbstractConfiguration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FlattenedJsonConfiguration extends AbstractConfiguration {

    private final Map<String, Object> configTab;

    public FlattenedJsonConfiguration(Map<String, Object> map) {
        this.configTab = new HashMap<>(map);
    }

    @Override
    protected void addPropertyDirect(String key, Object value) {
    }

    @Override
    public boolean isEmpty() {
        return configTab.isEmpty();
    }

    @Override
    public boolean containsKey(String key) {
        return configTab.containsKey(key);
    }

    @Override
    public Object getProperty(String key) {
        return configTab.get(key);
    }

    @Override
    public Iterator<String> getKeys() {
        return configTab.keySet().iterator();
    }

}
