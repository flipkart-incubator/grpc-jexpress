package com.flipkart.gjex.examples.helloworld.filter;

import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.http.HttpFilter;

import javax.inject.Named;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Named("CustomHeaderHttpFilter")
public class CustomHeaderHttpFilter extends HttpFilter {

    @Override
    public void doProcessRequest(ServletRequest servletRequest, RequestParams<Map<String, String>> requestParams) {
        super.doProcessRequest(servletRequest, requestParams);
    }

    @Override
    public void doProcessResponseHeaders(Map<String, String> responseHeaders) {
        super.doProcessResponseHeaders(responseHeaders);
    }

    @Override
    public void doProcessResponse(ServletResponse response) {
        super.doProcessResponse(response);
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.addHeader("x-custom-header", "custom-header-value");
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
