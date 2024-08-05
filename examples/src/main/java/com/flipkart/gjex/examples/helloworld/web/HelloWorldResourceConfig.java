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
package com.flipkart.gjex.examples.helloworld.web;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * ResourceConfig example for registering all custom web resources for a GJEX application.
 * @author regu.b
 *
 */
@Singleton
@Named("HelloWorldResourceConfig")
public class HelloWorldResourceConfig extends ResourceConfig {

    @Inject
    public HelloWorldResourceConfig (HelloWorldResource1 helloWorldresource1,
            HelloWorldResource2 helloWorldresource2) {
        register(helloWorldresource1);
        register(helloWorldresource2);
    }

}
