package com.flipkart.gjex.examples.helloworld.config;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.gjex.core.GJEXConfiguration;

import com.flipkart.gjex.db.DataSourceFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;


@Data
@EqualsAndHashCode(callSuper=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HelloWorldConfiguration extends GJEXConfiguration {

    @JsonProperty("api.properties")
    private Map<String, Object> apiProperties;

    @JsonProperty("task.properties")
    private Map<String, Object> taskProperties;

    @Valid
    @JsonProperty("database")
    private DataSourceFactory dataSourceFactory;
}
