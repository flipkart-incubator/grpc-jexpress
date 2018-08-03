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
import com.flipkart.gjex.core.setup.Bootstrap;
import com.flipkart.gjex.core.setup.Environment;
import com.flipkart.gjex.examples.helloworld.guice.HelloWorldModule;
import com.flipkart.gjex.guice.GuiceBundle;

/**
 * The GJEX HelloWorld sample Application
 * @author regu.b
 *
 */
public class HelloWorldApplication extends Application {

	@Override
    public String getName() {
        return "GJEX HelloWorld Application";
    }
	
	@Override
	public void initialize(Bootstrap bootstrap) {
		GuiceBundle guiceBundle = GuiceBundle.newBuilder()
				.addModules(new HelloWorldModule())
				.build();
		bootstrap.addBundle(guiceBundle);
	}

	@Override
	public void run(Environment environment) throws Exception {
		// doing nothing additionally
	}
	
	public static void main(String[] args) throws Exception {
		HelloWorldApplication app = new HelloWorldApplication();
		app.run(args);
	}

}
