package com.flipkart.gjex.core.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.requireNonNull;

public class YamlConfigurationFactory<T> implements ConfigurationFactory<T> {
    private final YAMLFactory yamlFactory;
    private Class<T> klass;
    private ObjectMapper objectMapper;

    public YamlConfigurationFactory(Class<T> klass, ObjectMapper objectMapper) {

        this.klass = klass;
        this.objectMapper = objectMapper;
        this.yamlFactory = new YAMLFactory();
    }

    @Override
    public T build(ConfigurationSourceProvider provider, String path) throws IOException {

        try (InputStream input = provider.open(requireNonNull(path))) {
            final JsonNode node = objectMapper.readTree(yamlFactory.createParser(input));
            return build(node);
        }
    }

    protected T build(JsonNode node) throws IOException {
        return objectMapper.readValue(new TreeTraversingParser(node), klass);
    }
}
