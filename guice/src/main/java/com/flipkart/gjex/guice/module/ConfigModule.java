/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.flipkart.gjex.guice.module;

import java.util.Map;

import com.flipkart.gjex.Constants;
import com.flipkart.gjex.core.config.GrpcConfig;
import com.flipkart.gjex.core.config.JExpressConfiguration;
import com.flipkart.gjex.core.config.YamlConfiguration;
import com.google.inject.AbstractModule;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.name.Names;

/**
 * <code>ConfigModule</code> is a Guice module for loading application JExpressConfiguration attributes
 *
 * @author regunath.balasubramanian
 */
public class ConfigModule extends AbstractModule {
    private JExpressConfiguration JExpressConfiguration;

    /**
     * Constructor to load the GJEX application startup JExpressConfiguration from System property or classpath
     */
    public ConfigModule(JExpressConfiguration JExpressConfiguration) {
        this.JExpressConfiguration = JExpressConfiguration;
    }

    /**
     * Performs concrete bindings for interfaces
     *
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void configure() {

        GrpcConfig grpcConfig = JExpressConfiguration.getGrpc();
        bind(Integer.class).annotatedWith(Names.named(Constants.GRPC_SERVER_PORT)).toInstance(grpcConfig.getServer().getPort());
        bind(Integer.class).annotatedWith(Names.named(Constants.DASHBOARD_SERVER_PORT)).toInstance(grpcConfig.getDashboard().getService().getPort());
        bind(Integer.class).annotatedWith(Names.named(Constants.API_SCHEDULEDEXECUTOR_THREADPOOL_SIZE)).toInstance(grpcConfig.getApi().getScheduledexecutor().getThreadpool().getSize());
        bind(Integer.class).annotatedWith(Names.named(Constants.DASHBOARD_SERVICE_ACCEPTORS)).toInstance(grpcConfig.getDashboard().getService().getAcceptors());
        bind(Integer.class).annotatedWith(Names.named(Constants.DASHBOARD_SERVICE_SELECTORS)).toInstance(grpcConfig.getDashboard().getService().getSelectors());
        bind(Integer.class).annotatedWith(Names.named(Constants.DASHBOARD_SERVICE_WORKERS)).toInstance(grpcConfig.getDashboard().getService().getWorkers());
        bind(Integer.class).annotatedWith(Names.named(Constants.API_SERVICE_PORT)).toInstance(grpcConfig.getApi().getService().getPort());
        bind(Integer.class).annotatedWith(Names.named(Constants.API_SERVICE_ACCEPTORS)).toInstance(grpcConfig.getApi().getService().getAcceptors());
        bind(Integer.class).annotatedWith(Names.named(Constants.API_SERVICE_WORKERS)).toInstance(grpcConfig.getApi().getService().getWorkers());
        bind(Integer.class).annotatedWith(Names.named(Constants.API_SERVICE_SELECTORS)).toInstance(grpcConfig.getApi().getService().getSelectors());

        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        for (Map.Entry<String, Object> entry : JExpressConfiguration.getGuiceBindableKeyVal().entrySet()) {
            Object propertyValue = entry.getValue();
            String propertyKey = entry.getKey();
            yamlConfiguration.addProperty(entry.getKey(), entry.getValue());
            LinkedBindingBuilder annotatedWith = bind(propertyValue.getClass()).annotatedWith(Names.named(propertyKey));
            annotatedWith.toInstance(propertyValue);
        }

        bind(org.apache.commons.configuration.Configuration.class).annotatedWith(Names.named(Constants.GLOBALCONFIG)).toInstance(yamlConfiguration);
    }
}
