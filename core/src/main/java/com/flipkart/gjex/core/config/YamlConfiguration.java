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

package com.flipkart.gjex.core.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

/**
 * Provides a configuration source for Apache Commons configuration that loads
 * values from a YAML file.
 *
 * @author regunath.balasubramanian
 *
 */

public class YamlConfiguration extends AbstractConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(YamlConfiguration.class);

    private Map<String, Object> configTab;

    public YamlConfiguration(String path) throws IOException {
        FileReader reader = new FileReader(path);
        load(reader);
        reader.close();
    }

    public YamlConfiguration(File file) throws IOException {
        FileReader reader = new FileReader(file);
        load(reader);
        reader.close();
    }

    public YamlConfiguration(URL url) throws IOException {
        InputStreamReader reader = new InputStreamReader(url.openStream());
        load(reader);
        reader.close();
    }

    public YamlConfiguration(BufferedReader reader) throws IOException {
        load(reader);
        reader.close();
    }
    ////////////////////////////////////////////////////////////////////////////
    // Methods of base class AbstractConfiguration

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

    @Override
    protected void addPropertyDirect(String key, Object value) {
        // TODO Auto-generated method stub

    }

    ////////////////////////////////////////////////////////////////////////////
    // Helper methods

    private void load(Reader in) {
        Yaml yaml = new Yaml();
        Map<?, ?> dom = (Map<?, ?>) yaml.load(in);
        configTab = new HashMap<>();
        flatten(null, dom);
        LOGGER.debug("yaml configuration loaded: {}", configTab);
    }

    private void flatten(String prefix, Map<?, ?> dom) {
        Set<?> keys = dom.keySet();
        for(Object key : keys) {
            String name = (prefix != null) ? (prefix + "." + key.toString()) : key.toString();
            Object value = dom.get(key);
            if(value instanceof Map) {
                flatten(name, (Map<?, ?>) value);
            }
            else {
                configTab.put(name, value);
            }
        }
    }
}