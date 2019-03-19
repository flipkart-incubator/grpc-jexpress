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
