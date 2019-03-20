package com.flipkart.gjex.examples.helloworld.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.gjex.core.GJEXConfiguration;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HelloWorldConfiguration extends GJEXConfiguration {

    @JsonProperty("api.properties")
    private Map<String, Object> apiProperties;

    @JsonProperty("task.properties")
    private Map<String, Object> taskProperties;

}
