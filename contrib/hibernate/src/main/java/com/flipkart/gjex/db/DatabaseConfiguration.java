package com.flipkart.gjex.db;

import com.flipkart.gjex.core.GJEXConfiguration;
import io.dropwizard.db.PooledDataSourceFactory;

public interface DatabaseConfiguration<T extends GJEXConfiguration> {

    PooledDataSourceFactory getDataSourceFactory(T configuration);
}
