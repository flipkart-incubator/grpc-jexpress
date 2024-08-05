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
package com.flipkart.gjex.core.web;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.flipkart.gjex.core.GJEXError;
import com.flipkart.gjex.core.tracing.TracingSamplerHolder;
import com.flipkart.gjex.core.web.dto.TracingConfiguration;

/**
 * Servlet resource for controlling/modifying tracing configuration
 * @author regu.b
 *
 */

@Singleton
@Path("/")
@Named
public class TracingResource {

    @Context
    private ServletContext servletContext;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public TracingConfiguration updateTracingConfiguration(TracingConfiguration tracingConfig) {
        TracingSamplerHolder tracingSamplerHolder = (TracingSamplerHolder) servletContext
                .getAttribute(TracingSamplerHolder.TRACING_SAMPLER_HOLDER_NAME);
        try {
            tracingSamplerHolder.updateTracingSampler(tracingConfig.getApiName(), tracingConfig.getComponentName(), tracingConfig.getSamplingRate());
        } catch (GJEXError error) {
            throw new ResourceException(error.getMessage());
        }
        return tracingConfig;
    }
}
