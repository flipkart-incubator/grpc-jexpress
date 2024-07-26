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

import com.google.common.collect.Maps;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FlattenedConfigurationTest {

    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        Map<String, Object> configMap = Maps.newHashMap();
        configMap.put("Grpc.server.port", 50051);
        configMap.put("Dashboard.server.port", 9999);
        configuration = new FlattenedConfiguration(configMap);
    }

    @Test
    public void getKeySuccess() {
        assertThat(configuration.getInt("Grpc.server.port")).isEqualTo(50051);
        assertThat(configuration.getInt("Dashboard.server.port")).isEqualTo(9999);
    }

    @Test
    public void getKeyFailure() {
        assertThatThrownBy(() -> configuration.getInt("Grpc.key.does.not.exist")).isInstanceOf(NoSuchElementException.class);
    }
}
