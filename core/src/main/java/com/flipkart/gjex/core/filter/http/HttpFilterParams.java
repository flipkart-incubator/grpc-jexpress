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
package com.flipkart.gjex.core.filter.http;

import lombok.Builder;
import lombok.Data;

/**
 * Encapsulates the parameters necessary for creating HTTP filters within the GJEX framework.
 * This class serves as a data holder for filter configurations, including the filter instance itself
 * and the path specification to which the filter applies.
 * <p>
 * The {@code filter} field holds an instance of a class implementing the {@link javax.servlet.Filter} interface,
 * enabling the interception and processing of requests and responses in the web application.
 * The {@code pathSpec} field specifies the URL pattern(s) that the filter will be applied to, allowing for
 * targeted filtering based on request paths.
 * </p>
 *
 * @author ajay.jalgaonkar
 */
@Data
@Builder
public class HttpFilterParams {
  // The filter instance to be applied.
  private final HttpFilter filter;

  // The URL pattern(s) the filter applies to.
  private final String pathSpec;
}
