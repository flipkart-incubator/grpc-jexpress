package com.flipkart.gjex.http.interceptor;

import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.http.HttpFilter;
import com.flipkart.gjex.core.filter.http.HttpFilterParams;
import org.eclipse.jetty.http.pathmap.ServletPathSpec;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
@Named("HttpFilterInterceptor")
public class HttpFilterInterceptor implements javax.servlet.Filter {

    private static class ServletPathFiltersHolder {
        ServletPathSpec spec;
        List<HttpFilter> filters;

        public ServletPathFiltersHolder(ServletPathSpec spec, List<HttpFilter> filters) {
            this.spec = spec;
            this.filters = filters;
        }
    }

    /**
     * Map of Filter instances mapped to Service and its method
     */
    @SuppressWarnings("rawtypes")
    private Map<String, ServletPathFiltersHolder> filtersMap = new HashMap<>();
    private Map<ServletPathSpec, List<HttpFilter>> pathSpecToFilterMap = new HashMap<>();

    public void registerFilters(List<HttpFilterParams> httpFilterParamsList) {
        for (HttpFilterParams httpFilterParams: httpFilterParamsList){
            if (!filtersMap.containsKey(httpFilterParams.getPathSpec())){
                ServletPathSpec spec = new ServletPathSpec(httpFilterParams.getPathSpec());
                filtersMap.put(httpFilterParams.getPathSpec(), new ServletPathFiltersHolder(spec,
                    new ArrayList<>()));
            }
            filtersMap.get(httpFilterParams.getPathSpec()).filters.add(httpFilterParams.getFilter());
        }
        for (ServletPathFiltersHolder servletPathFiltersHolder : filtersMap.values()){
            pathSpecToFilterMap.computeIfAbsent(servletPathFiltersHolder.spec,
                k-> new ArrayList<>()).addAll(servletPathFiltersHolder.filters);
        }
    }

    public void init(FilterConfig filterConfig) throws ServletException {}

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

        List<HttpFilter> filters = new ArrayList<>();
        RequestParams.RequestParamsBuilder<Set<String>> requestParamsBuilder = RequestParams.builder();
        try {
            if (request instanceof HttpServletRequest){
                HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                filters = getMatchingFilters(httpServletRequest.getRequestURI());
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
    protected String getClientIp(ServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String xForwardedFor = ((HttpServletRequest) request).getHeader("X-Forwarded-For");
        if (xForwardedFor != null) {
            remoteAddr = xForwardedFor.split(",")[0];
        }
        return remoteAddr;
    }

    protected List<HttpFilter> getMatchingFilters(String path) {
        return pathSpecToFilterMap.keySet().stream().filter(key -> key.matches(path))
            .map(k-> pathSpecToFilterMap.get(k)).flatMap(List::stream)
            .map(filter -> filter.getInstance()).collect(Collectors.toList());
    }

    @Override
    public void destroy() {

    }
}
