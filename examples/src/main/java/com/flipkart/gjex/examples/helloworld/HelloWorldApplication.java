/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flipkart.gjex.examples.helloworld;

import com.flipkart.gjex.core.Application;
import com.flipkart.gjex.core.GJEXConfiguration;
import com.flipkart.gjex.core.setup.Bootstrap;
import com.flipkart.gjex.core.setup.Environment;
import com.flipkart.gjex.examples.helloworld.config.HelloWorldConfiguration;
import com.flipkart.gjex.examples.helloworld.entity.DummyEntity;
import com.flipkart.gjex.examples.helloworld.guice.HelloWorldModule;
import com.flipkart.gjex.guice.GuiceBundle;
import com.flipkart.gjex.hibernate.HibernateBundle;
import io.dropwizard.db.PooledDataSourceFactory;

import java.util.Map;

/**
 * The GJEX HelloWorld sample Application
 *
 * @author regu.b
 */
@SuppressWarnings("rawtypes")
public class HelloWorldApplication extends Application<HelloWorldConfiguration, Map> {

    private HibernateBundle<HelloWorldConfiguration, Map> hibernateBundle;

    @Override
    public String getName() {
        return "GJEX HelloWorld Application";
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration, Map> bootstrap) {

        hibernateBundle =
            new HibernateBundle<HelloWorldConfiguration, Map>(DummyEntity.class) {
                @Override
                public PooledDataSourceFactory getDataSourceFactory(GJEXConfiguration gjexConfiguration) {
                    HelloWorldConfiguration configuration = (HelloWorldConfiguration) gjexConfiguration;
                    return configuration.getDataSourceFactory();
                }

                @Override
                protected String name() {
                    return "hibernate";
                }
            };

        bootstrap.addBundle(hibernateBundle);
        GuiceBundle<HelloWorldConfiguration, Map> guiceBundle = new GuiceBundle.Builder<HelloWorldConfiguration, Map>()
            .setConfigClass(HelloWorldConfiguration.class)
            .addModules(new HelloWorldModule(hibernateBundle.getSessionFactory()))
            .build();
        bootstrap.addBundle(guiceBundle);
    }

    @Override
    public void run(HelloWorldConfiguration configuration, Map configMap, Environment environment) throws Exception {
    }

    public static void main(String[] args) throws Exception {
        HelloWorldApplication app = new HelloWorldApplication();
        app.run(args);
    }
}
