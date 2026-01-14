package com.flipkart.gjex.core.config;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * A ConfigurationSourceProvider that substitutes environment variables in configuration files.
 * Supports ${ENV_VAR} syntax for environment variable placeholders.
 */
public class EnvironmentSubstitutingSourceProvider implements ConfigurationSourceProvider {
  private final ConfigurationSourceProvider delegate;
  private final StrSubstitutor substitutor;

  public EnvironmentSubstitutingSourceProvider(ConfigurationSourceProvider delegate) {
    this.delegate = delegate;
    this.substitutor = new StrSubstitutor(System.getenv());
  }

  @Override
  public InputStream open(String path) throws IOException {
    try (InputStream input = delegate.open(path)) {
      String content = new String(input.readAllBytes(), StandardCharsets.UTF_8);
      String substituted = substitutor.replace(content);
      return new ByteArrayInputStream(substituted.getBytes(StandardCharsets.UTF_8));
    }
  }
}

