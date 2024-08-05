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
package com.flipkart.gjex.core.filter.grpc;

import com.flipkart.gjex.core.context.GJEXContext;

import io.grpc.Metadata;

/**
 * Provides way for GJEX applications to access gRPC headers within the service implementation classes
 *
 * @author regu.b
 *
 */
public class ApplicationHeaders {

    /**
    * Returns forwarded headers, if any
    * @return null or forwarded headers
    */
    public static Metadata getHeaders() {
        return GJEXContext.activeHeaders();
    }

}
