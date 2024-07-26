package com.flipkart.gjex.core.filter.http;

import com.flipkart.gjex.core.logging.Logging;
import org.eclipse.jetty.http.pathmap.ServletPathSpec;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenericHttpFilterInterceptor implements javax.servlet.Filter, Logging {

    /**
     * Map of Filter instances mapped to Service and its method
     */
    @SuppressWarnings("rawtypes")
    protected Map<String, ServletPathJavaxFiltersHolder> filtersMap = new HashMap<>();
    protected Map<ServletPathSpec, List<FilterWrapper>> pathSpecToFilterMap = new HashMap<>();

    public GenericHttpFilterInterceptor(Map<String, ServletPathJavaxFiltersHolder> filtersMap) {
        this.filtersMap = filtersMap;
    }

    protected static class ServletPathJavaxFiltersHolder {
        ServletPathSpec spec;
        List<FilterWrapper> filters;

        public ServletPathJavaxFiltersHolder(ServletPathSpec spec, List<FilterWrapper> filters) {
            this.spec = spec;
            this.filters = filters;
        }
    }

    public void init(FilterConfig filterConfig) throws ServletException {
       for (ServletPathJavaxFiltersHolder servletPathJavaxFiltersHolder : filtersMap.values()){
           pathSpecToFilterMap.computeIfAbsent(servletPathJavaxFiltersHolder.spec,
               k-> new ArrayList<>()).addAll(servletPathJavaxFiltersHolder.filters);
       }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                               FilterChain chain) throws IOException, ServletException {
        List<FilterWrapper> filters = new ArrayList<>();
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            filters = getMatchingFilters(pathSpecToFilterMap, httpServletRequest.getRequestURI());
        }
        filters.forEach(filter -> {
            try {
                filter.doFilter(request, response, chain);
            } catch (IOException e) {
                error(e.getMessage());
                throw new RuntimeException(e);
            } catch (ServletException e) {
                error(e.getMessage());
                throw new RuntimeException(e);
            }
        });
        chain.doFilter(request, response);
    }

    protected static List<FilterWrapper> getMatchingFilters(Map<ServletPathSpec,
        List<FilterWrapper>> pathSpecToFilterMap, String path) {
        return pathSpecToFilterMap.keySet().stream().filter(key -> key.matches(path))
            .map(pathSpecToFilterMap::get).flatMap(List::stream)
            .map(FilterWrapper::getInstance).collect(Collectors.toList());
    }

    @Override
    public void destroy() {

    }
}
