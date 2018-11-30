package com.flipkart.gjex.core.config;

import java.io.IOException;

public interface ConfigurationFactory<T> {
    T build(ConfigurationSourceProvider provider, String path) throws IOException;
}
