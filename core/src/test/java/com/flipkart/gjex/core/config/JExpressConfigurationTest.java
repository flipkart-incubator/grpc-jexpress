package com.flipkart.gjex.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class JExpressConfigurationTest {
    @Test
    public void configTest() throws IOException, URISyntaxException {
        YamlConfigurationFactory<SampleConfig> factory = new YamlConfigurationFactory<SampleConfig>(SampleConfig.class, new ObjectMapper());

        File file = new File(Resources.getResource("yaml/sample_config.yml").toURI());
        SampleConfig sampleConfig = factory.build(new FileConfigurationSourceProvider(), file.getAbsolutePath());

        Assert.assertEquals("Port should be equal", sampleConfig.getGrpc().getServer().getPort(),50052);
        Assert.assertTrue(sampleConfig.getGuiceBindableKeyVal().containsKey("testing"));
    }

}
