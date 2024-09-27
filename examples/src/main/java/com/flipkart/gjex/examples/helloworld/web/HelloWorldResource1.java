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

import com.flipkart.gjex.core.tracing.Traced;
import com.flipkart.gjex.examples.helloworld.bean.HelloBean;
import com.flipkart.gjex.examples.helloworld.service.HelloBeanService;
import io.grpc.examples.helloworld.GreeterGrpc;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Servlet Resource example for control path API
 * @author regu.b
 *
 */

@Singleton
@Path("/api")
@Named
public class HelloWorldResource1 {

    /** Injected business logic class where validation is performed */
    private HelloBeanService helloBeanService = new HelloBeanService();


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/hellocontrol1")
	public Response performHelloControl() {

        // invoke business logic implemented in a separate injected class
        helloBeanService.sayHelloInBean(new HelloBean("hello",10));

		return Response.status(Response.Status.OK).entity("Hello Control 1 invoked").build();
	}





}
