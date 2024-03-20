package com.flipkart.gjex.examples.helloworld.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.gjex.core.GJEXConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;


@Data
@EqualsAndHashCode(callSuper=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HelloWorldConfiguration extends GJEXConfiguration {

    @JsonProperty("api.properties")
    private Map<String, Object> apiProperties;

    @JsonProperty("task.properties")
    private Map<String, Object> taskProperties;

    @JsonProperty("rotationManagement")
    private Map<String, Object> rotationManagement;

    public Map<String, Object> getApiProperties() {
        return apiProperties;
    }

    public void setApiProperties(Map<String, Object> apiProperties) {
        this.apiProperties = apiProperties;
    }

    public Map<String, Object> getTaskProperties() {
        return taskProperties;
    }

    public void setTaskProperties(Map<String, Object> taskProperties) {
        this.taskProperties = taskProperties;
    }

    public Map<String, Object> getRotationManagement() {
        return rotationManagement;
    }

    public void setRotationManagement(Map<String, Object> rotationManagement) {
        this.rotationManagement = rotationManagement;
    }

}
