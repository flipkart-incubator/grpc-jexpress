package com.flipkart.gjex.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.util.Maps;
import com.google.common.io.Resources;
import javafx.util.Pair;
import org.assertj.core.data.MapEntry;
import org.junit.Test;

import javax.validation.Validator;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public abstract class BaseConfigurationFactoryTest {

    private static final String NEWLINE = System.lineSeparator();

    public static class ExampleConfig extends GJEXConfiguration {

        @NotNull
        @JsonProperty
        private String name = "";

        @JsonProperty
        @Max(20)
        private int age;

        @JsonProperty
        private Map<String, String> properties = Collections.emptyMap();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Map<String, String> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, String> properties) {
            this.properties = properties;
        }
    }

    public static class ExampleConfigWithDefaults extends GJEXConfiguration {

        private String name = "GJEX";

        private int age = 12;

        private @JsonProperty
        Map<String, String> properties = Maps.of("k1", "v1", "k2", "v2");

    }

    protected final Validator validator = null; // Validation.buildDefaultValidatorFactory().getValidator();

    protected File malformedFile = new File("/");
    protected File emptyFile = new File("/");
    protected File validFile = new File("/");
    protected File typoFile = new File("/");
    protected File wrongTypeFile = new File("/");
    protected File malformedAdvancedFile = new File("/");

    protected ConfigurationFactory<ExampleConfig, Map> factory = new ConfigurationFactory<ExampleConfig, Map>() {

        @Override
        public Pair<ExampleConfig, Map> build(ConfigurationSourceProvider provider, String path) throws IOException, ConfigurationException {
            return new Pair<>(new ExampleConfig(), new HashMap());
        }

        @Override
        public Pair<ExampleConfig, Map> build() throws IOException, ConfigurationException {
            return new Pair<>(new ExampleConfig(), new HashMap());
        }
    };

    protected static File resourceFileName(String resourceName) throws URISyntaxException {
        return new File(Resources.getResource(resourceName).toURI());
    }


    @Test
    public void throwsAnExceptionOnMalformedFiles() throws Exception {
        factory.build(malformedFile);
        failBecauseExceptionWasNotThrown(ConfigurationParsingException.class);
    }

    @Test
    public void throwsAnExceptionOnEmptyFiles() throws Exception {
        try {
            factory.build(emptyFile);
            failBecauseExceptionWasNotThrown(ConfigurationParsingException.class);
        } catch (ConfigurationParsingException e) {
            assertThat(e.getMessage())
                    .containsOnlyOnce(" * GJEXConfiguration at " + emptyFile.toString() + " must not be empty");
        }
    }

    @Test
    public void incorrectTypeIsFound() throws Exception {
        assertThatThrownBy(() -> factory.build(wrongTypeFile))
                .isInstanceOf(ConfigurationParsingException.class)
                .hasMessage(String.format("%s has an error:" + NEWLINE +
                        "  * Incorrect type of value at: age; is of type: String, expected: int" + NEWLINE, wrongTypeFile));
    }

    @Test
    public void loadsValidConfigFiles() throws Exception {
        final Pair<ExampleConfig, Map> result = factory.build(validFile);
        ExampleConfig example = result.getKey();
        assertThat(example.getName()).isEqualTo("GJEX");
        assertThat(example.getAge()).isEqualTo(12);
        assertThat(example.getProperties())
                .contains(MapEntry.entry("k1", "v1"),
                        MapEntry.entry("settings.enabled", "true"));

        Map<String, Object> configMap = (Map<String, Object>) result.getValue();

        assertThat(configMap.size()).isEqualTo(15);
        assertThat(configMap.get("Grpc-server.port")).isEqualTo(50051);
        assertThat(configMap.get("Dashboard-service.port")).isEqualTo(9999);
        assertThat(configMap.get("Dashboard-service.workers")).isEqualTo(30);
    }

    @Test
    public void printsDetailedInformationOnMalformedContent() throws Exception {
        factory.build(malformedAdvancedFile);
    }

}