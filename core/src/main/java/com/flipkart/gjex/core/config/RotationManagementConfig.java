package com.flipkart.gjex.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.inject.Singleton;

@Singleton
public class RotationManagementConfig {
  @JsonProperty("defaultRotationState")
  private boolean defaultRotationState;

  public boolean getDefaultRotationState() {
    return defaultRotationState;
  }
}
