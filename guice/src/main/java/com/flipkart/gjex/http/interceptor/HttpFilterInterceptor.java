package com.flipkart.gjex.http.interceptor;

import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.grpc.GrpcFilter;
import com.flipkart.gjex.core.filter.http.HttpFilter;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
@Named("HttpFilterInterceptor")
public class HttpFilterInterceptor implements javax.servlet.Filter{

    /**
     * Map of Filter instances mapped to Service and its method
     */
    @SuppressWarnings("rawtypes")
    private final List<HttpFilter> filtersMap = new ArrayList<>();

    public void registerFilters(List<HttpFilter> filters) {
        filtersMap.addAll(filters);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    /**
     * The core method that processes incoming requests and responses. It captures the request and response objects,
     * extracts and builds request parameters including client IP and request headers, and invokes the
     * {@link HttpFilter#doProcessRequest(ServletRequest, RequestParams)} method for further processing.
     * Finally, it ensures that the response is processed by invoking {@link HttpFilter#doProcessResponse(ServletResponse)}.
     *
     * @param request  The incoming ServletRequest
     * @param response The outgoing ServletResponse
     * @param chain         The filter chain to which the request and response should be passed for further processing
     * @throws IOException      if an I/O error occurs during the filter chain execution
     * @throws ServletException if the request could not be handled
     */
    @Override
    public final void doFilter(ServletRequest request, ServletResponse response,
                               FilterChain chain) throws IOException, ServletException {
        List<HttpFilter> filters = filtersMap.stream().map(HttpFilter::getInstance).toList();
        try {
            RequestParams.RequestParamsBuilder<Set<String>> requestParamsBuilder = RequestParams.builder();
            if (request instanceof HttpServletRequest){
                HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                Set<String> headersNames = new HashSet<>(Collections.list(httpServletRequest.getHeaderNames()));
                requestParamsBuilder.metadata(headersNames);
                requestParamsBuilder.clientIp(getClientIp(request));
                requestParamsBuilder.resourcePath(httpServletRequest.getRequestURI());
            }
            RequestParams<Set<String>> requestParams = requestParamsBuilder.build();
            filters.forEach(filter -> filter.doProcessRequest(request, requestParams));
            chain.doFilter(request,response);
        } finally {
            if (response instanceof HttpServletResponse){
                HttpServletResponse httpServletResponse = (HttpServletResponse)response;
                filters.forEach(filter -> filter.doProcessResponseHeaders(new HashSet<>(httpServletResponse.getHeaderNames())));
            }
            filters.forEach(filter -> filter.doProcessResponse(response));
        }
    }

    /**
     * Utility method to extract the real client IP address from the ServletRequest. It checks for the
     * "X-Forwarded-For" header to support clients connecting through a proxy.
     *
     * @param request The ServletRequest object containing the client's request
     * @return The real IP address of the client
     */
    private String getClientIp(ServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String xForwardedFor = ((HttpServletRequest) request).getHeader("X-Forwarded-For");
        if (xForwardedFor != null) {
            remoteAddr = xForwardedFor.split(",")[0];
        }
        return remoteAddr;
    }

    @Override
    public void destroy() {

    }
}
