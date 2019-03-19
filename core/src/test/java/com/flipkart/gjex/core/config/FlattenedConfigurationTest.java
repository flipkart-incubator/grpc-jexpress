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