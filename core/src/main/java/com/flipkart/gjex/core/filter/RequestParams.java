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
package com.flipkart.gjex.core.filter;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents the parameters of a request within the GJEX framework.
 * This class encapsulates common request parameters such as client IP, resource path,
 * and any additional metadata associated with the request.
 *
 * @param <M> The type of the metadata associated with the request. This allows for flexibility
 *            in the type of metadata that can be attached to a request, making the class
 *            adaptable to various needs.
 *
 * @author ajay.jalgaonkar
 */
@Getter
@Builder
public class RequestParams<M> {
  // IP address of the client making the request.
  String clientIp;

  // Path of the resource being requested.
  String resourcePath;

  // method of the request.
  String method;

  // Metadata associated with the request, of generic type M.
  M metadata;
}
