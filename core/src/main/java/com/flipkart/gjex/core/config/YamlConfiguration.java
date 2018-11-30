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

    private Map<String, Object> configTab = new HashMap<>();

    public YamlConfiguration() {}

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
        this.configTab.put(key, value);

    }
}