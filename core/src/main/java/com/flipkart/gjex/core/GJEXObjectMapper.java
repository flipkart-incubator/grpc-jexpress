package com.flipkart.gjex.core;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GJEXObjectMapper {

    private GJEXObjectMapper() { /* singleton */ }

    /**
     * Creates a new {@link ObjectMapper}
     */
    public static ObjectMapper newObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        return configure(mapper);
    }

    /**
     * Creates a new {@link ObjectMapper} with a custom {@link com.fasterxml.jackson.core.JsonFactory}
     *
     * @param jsonFactory instance of {@link com.fasterxml.jackson.core.JsonFactory} to use
     * for the created {@link com.fasterxml.jackson.databind.ObjectMapper} instance.
     */
    public static ObjectMapper newObjectMapper(JsonFactory jsonFactory) {
        final ObjectMapper mapper = new ObjectMapper(jsonFactory);
        return configure(mapper);
    }

    private static ObjectMapper configure(ObjectMapper mapper) {
        return mapper;
    }
}
