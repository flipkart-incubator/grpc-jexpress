package com.flipkart.gjex.db;

import com.flipkart.gjex.core.GJEXConfiguration;

public interface DatabaseConfiguration<T extends GJEXConfiguration> {
    PooledDataSourceFactory getDataSourceFactory(T var1);
}
