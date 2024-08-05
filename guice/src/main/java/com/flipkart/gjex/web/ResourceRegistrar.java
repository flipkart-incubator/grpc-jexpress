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
package com.flipkart.gjex.web;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.flipkart.gjex.Constants;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.web.ResourceException;

/**
 * <code>ResourceRegistrar</code> provides ability to wire GJEX application specific web resources to the /api Http1.x end-point
 * hosted by this GJEX runtime.
 * This ResourceRegistrar may be used by GJEX applications to configure additional control path APIs similar to Health check.
 *
 * @author regunath.balasubramanian
 */

@Singleton
public class ResourceRegistrar implements Logging {

    private final ServletContextHandler context;
    private final JacksonJaxbJsonProvider jaxbProvider;

    @Inject
    public ResourceRegistrar(@Named("ApiServletContext")ServletContextHandler context,
            @Named("JSONMarshallingProvider")JacksonJaxbJsonProvider jaxbProvider) {
        this.context = context;
        this.jaxbProvider = jaxbProvider;
    }

    public void registerResources(List<ResourceConfig> resourceConfigs) throws Exception {
        ResourceConfig uniqueResourceConfig = null;
        for (ResourceConfig resourceConfig : resourceConfigs) {
            // we check to ensure we are not adding GJEX core application resources again
            if (resourceConfig.getApplicationName() == null ||
                    !resourceConfig.getApplicationName().equalsIgnoreCase(Constants.GJEX_CORE_APPLICATION)) {
                if (uniqueResourceConfig == null) {
                    uniqueResourceConfig = resourceConfig;
                } else {
                    throw new ResourceException("Multiple ResourceConfig instances configured for this GJEX application. Only one may be configured.");
                }
            }
        }
        if (uniqueResourceConfig == null) {
            return;
        }
        uniqueResourceConfig.register(jaxbProvider);
        ServletHolder servlet = new ServletHolder(new ServletContainer(uniqueResourceConfig));
        context.addServlet(servlet, "/*");
    }

}
