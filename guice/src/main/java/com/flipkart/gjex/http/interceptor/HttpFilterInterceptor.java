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
import java.util.*;
import java.util.stream.Collectors;

@Singleton
@Named("HttpFilterInterceptor")
public class HttpFilterInterceptor implements javax.servlet.Filter {

    private static class ServletPathFiltersHolder {
        ServletPathSpec spec;
        HttpFilter filter;

        public ServletPathFiltersHolder(ServletPathSpec spec, HttpFilter filter) {
            this.spec = spec;
            this.filter = filter;
        }
    }

    /**
     * Map of Filter instances mapped to Service and its method
     */
    private final List<ServletPathFiltersHolder> pathFiltersHolders = new ArrayList<>();

    public void registerFilters(List<HttpFilterParams> httpFilterParamsList) {
        for(HttpFilterParams p : httpFilterParamsList){
            ServletPathSpec spec = new ServletPathSpec(p.getPathSpec());
            pathFiltersHolders.add(new ServletPathFiltersHolder(spec, p.getFilter()));
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
        RequestParams.RequestParamsBuilder<Map<String,String>> requestParamsBuilder = RequestParams.builder();
        try {
            if (request instanceof HttpServletRequest){
                HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                filters = getMatchingFilters(httpServletRequest.getRequestURI());

                Map<String, String> headers = Collections.list(httpServletRequest.getHeaderNames())
                    .stream().collect(Collectors.toMap(h -> h, httpServletRequest::getHeader));
                requestParamsBuilder.metadata(headers);
                requestParamsBuilder.clientIp(getClientIp(request));
                requestParamsBuilder.method(httpServletRequest.getMethod());
                requestParamsBuilder.resourcePath(getFullURL(httpServletRequest));
            }
            RequestParams<Map<String, String>> requestParams = requestParamsBuilder.build();
            filters.forEach(filter -> filter.doProcessRequest(request, requestParams));
            chain.doFilter(request, response);
        } finally {
            if (response instanceof HttpServletResponse) {
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                Map<String, String> headers = httpServletResponse.getHeaderNames()
                    .stream().collect(Collectors.toMap(h -> h, httpServletResponse::getHeader));
                filters.forEach(filter -> filter.doProcessResponseHeaders(headers));
            }
            filters.forEach(filter -> filter.doProcessResponse(response));
        }
    }

    /**
     * Constructs the full URL from the HttpServletRequest.
     *
     * @param request The HttpServletRequest object.
     * @return The full URL as a string.
     */
    protected static String getFullURL(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURI());
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    /**
     * Utility method to extract the real client IP address from the ServletRequest. It checks for the
     * "X-Forwarded-For" header to support clients connecting through a proxy.
     *
     * @param request The ServletRequest object containing the client's request
     * @return The real IP address of the client
     */
    protected static String getClientIp(ServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String xForwardedFor = ((HttpServletRequest) request).getHeader("X-Forwarded-For");
        if (xForwardedFor != null) {
            remoteAddr = xForwardedFor.split(",")[0];
        }
        return remoteAddr;
    }

    protected List<HttpFilter> getMatchingFilters(String path) {
        return pathFiltersHolders.stream().filter(t -> t.spec.matches(path))
            .map(t-> t.filter)
            .map(HttpFilter::getInstance).collect(Collectors.toList());
    }

    @Override
    public void destroy() {

    }
}
