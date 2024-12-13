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
package com.flipkart.gjex.examples.helloworld.filter;

import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.http.HttpFilter;

import javax.inject.Named;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Custom HTTP filter that adds custom headers to the response.
 */
@Named("CustomHeaderHttpFilter")
public class CustomHeaderHttpFilter extends HttpFilter {

    @Override
    public void doProcessRequest(ServletRequest servletRequest, RequestParams<Map<String, String>> requestParams) {
        super.doProcessRequest(servletRequest, requestParams);
    }

    /**
     * Processes the response headers and adds a custom header.
     *
     * @param responseHeaders the response headers
     */
    @Override
    public void doProcessResponseHeaders(Map<String, String> responseHeaders) {
        super.doProcessResponseHeaders(responseHeaders);
        responseHeaders.put("x-custom-header1", "value1");
    }

    /**
     * Processes the response and also adds a custom header.
     *
     * @param response the servlet response
     * @return
     */
    @Override
    public ServletResponse doProcessResponse(ServletResponse response) {
        super.doProcessResponse(response);
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.addHeader("x-custom-header2", "value2");
        return response;
    }

    @Override
    public void doHandleException(Exception e) {
        super.doHandleException(e);
    }

    @Override
    public HttpFilter getInstance() {
        return new CustomHeaderHttpFilter();
    }
}
