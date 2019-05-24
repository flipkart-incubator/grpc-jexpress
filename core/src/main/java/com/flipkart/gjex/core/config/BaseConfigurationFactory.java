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

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.util.Pair;

/**
 * A generic factory class for loading configuration files, binding them to configuration objects, and
 * validating their constraints.
 *
 * @param <T> the type of the configuration object to produce
 * @param <U> Given configuration as a simple plain map
 */
@SuppressWarnings("rawtypes")
public abstract class BaseConfigurationFactory<T extends GJEXConfiguration, U extends Map> implements ConfigurationFactory<T, U> {

    protected final Class<T> klass;
    protected final ObjectMapper objectMapper;
    private final Validator validator;
    protected final String formatName;
    private final JsonFactory parserFactory;

    /**
     * Creates a new configuration factory for the given class.
     * @param parserFactory  the factory that creates the parser used
     * @param formatName     the name of the format parsed by this factory (used in exceptions)
     * @param klass          the configuration class
     * @param validator      the validator to use
     */
    public BaseConfigurationFactory(JsonFactory parserFactory,
                                    String formatName,
                                    Class<T> klass,
                                    Validator validator,
                                    ObjectMapper objectMapper) {
        this.klass = klass;
        this.formatName = formatName;
        this.objectMapper = objectMapper;
        this.parserFactory = parserFactory;
        this.validator = validator;
    }

    @Override
    public Pair<T, U> build(ConfigurationSourceProvider provider, String path) throws IOException, ConfigurationException {
        try (InputStream input = provider.open(requireNonNull(path))) {
            final JsonNode node = objectMapper.readTree(createParser(input));
            if (node == null) {
                throw ConfigurationParsingException
                        .builder("GJEXConfiguration at " + path + " must not be empty")
                        .build(path);
            }
            return build(node, path);
        } catch (JsonParseException e) {
            throw ConfigurationParsingException
                    .builder("Malformed " + formatName)
                    .setCause(e)
                    .setLocation(e.getLocation())
                    .setDetail(e.getMessage())
                    .build(path);
        }
    }

    protected JsonParser createParser(InputStream input) throws IOException {
        return parserFactory.createParser(input);
    }

    @Override
    public Pair<T, U> build() throws IOException, ConfigurationException {
        try {
            final JsonNode node = objectMapper.valueToTree(klass.newInstance());
            return build(node, "Default configuration");
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
            throw new IllegalArgumentException("Unable create an instance " +
                    "of the configuration class: '" + klass.getCanonicalName() + "'", e);
        }
    }

    protected Pair<T, U> build(JsonNode node, String path) throws IOException, ConfigurationException {
        try {
            final T config = objectMapper.readValue(new TreeTraversingParser(node), klass);
            final U configMap = objectMapper.readValue(objectMapper.writeValueAsString(node), new TypeReference<U>() {});
            validate(path, config);
            return new Pair<>(config, configMap);
        } catch (UnrecognizedPropertyException e) {
            final List<String> properties = e.getKnownPropertyIds().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
            throw ConfigurationParsingException.builder("Unrecognized field")
                    .setFieldPath(e.getPath())
                    .setLocation(e.getLocation())
                    .addSuggestions(properties)
                    .setSuggestionBase(e.getPropertyName())
                    .setCause(e)
                    .build(path);
        } catch (InvalidFormatException e) {
            final String sourceType = e.getValue().getClass().getSimpleName();
            final String targetType = e.getTargetType().getSimpleName();
            throw ConfigurationParsingException.builder("Incorrect type of value")
                    .setDetail("is of type: " + sourceType + ", expected: " + targetType)
                    .setLocation(e.getLocation())
                    .setFieldPath(e.getPath())
                    .setCause(e)
                    .build(path);
        } catch (JsonMappingException e) {
            throw ConfigurationParsingException.builder("Failed to parse configuration")
                    .setDetail(e.getMessage())
                    .setFieldPath(e.getPath())
                    .setLocation(e.getLocation())
                    .setCause(e)
                    .build(path);
        }
    }

    private void validate(String path, T config) throws ConfigurationValidationException {
        if (validator != null) {
            final Set<ConstraintViolation<T>> violations = validator.validate(config);
            if (!violations.isEmpty()) {
                throw new ConfigurationValidationException(path, violations);
            }
        }
    }
}
