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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.util.Pair;
import com.github.wnameless.json.flattener.PrintMode;
import com.github.wnameless.json.unflattener.JsonUnflattener;

@SuppressWarnings("rawtypes")
public interface ConfigurationFactory<T extends GJEXConfiguration, U extends Map> {

    /**
    * Loads, parses, binds, and validates a configuration object.
    *
    * @param provider the provider to to use for reading configuration files
    * @param path     the path of the configuration file
    * @return A pair of validated configuration object (T) and a map representing the configuration (U)
    * @throws IOException            if there is an error reading the file
    * @throws ConfigurationException if there is an error parsing or validating the file
    */
    Pair<T, U> build(ConfigurationSourceProvider provider, String path) throws IOException, ConfigurationException;

    /**
    * Loads, parses, binds, and validates a configuration object from a file.
    *
    * @param file the path of the configuration file
    * @return A pair of validated configuration object (T) and a map representing the configuration (U)
    * @throws IOException            if there is an error reading the file
    * @throws ConfigurationException if there is an error parsing or validating the file
    */
    default Pair<T, U> build(File file) throws IOException, ConfigurationException {
        return build(new FileConfigurationSourceProvider(), file.toString());
    }

    /**
    * Loads, parses, binds, and validates a configuration object from an empty document.
    *
    * @return A pair of validated configuration object (T) and a map representing configuration (U)
    * @throws IOException            if there is an error reading the file
    * @throws ConfigurationException if there is an error parsing or validating the file
    */
    Pair<T, U> build() throws IOException, ConfigurationException;

    /**
    * This function returns an un-flattened json for given flattened json (json flattened using separator)
    * @param flattenedJson flattened json
    * @param separator character with which @flattenedJson has been flattened
    * @return Un-flattened json as string
    */
    default String getUnFlattenedJson(String flattenedJson, char separator) {
        return new JsonUnflattener(flattenedJson).
                withPrintMode(PrintMode.PRETTY).
                withSeparator(separator).
                unflatten();
    }

}
