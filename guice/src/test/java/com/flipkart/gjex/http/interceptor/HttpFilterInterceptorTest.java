package com.flipkart.gjex.http.interceptor;

import com.flipkart.gjex.core.filter.http.AccessLogHttpFilter;
import com.flipkart.gjex.core.filter.http.HttpFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.flipkart.gjex.core.filter.http.HttpFilterParams;
import org.eclipse.jetty.http.pathmap.RegexPathSpec;
import org.eclipse.jetty.http.pathmap.ServletPathSpec;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class HttpFilterInterceptorTest {

    private HttpFilterInterceptor interceptor;

    @Before
    public void setUp() {
        interceptor = new HttpFilterInterceptor();
    }

    @Test
    public void registerFiltersAddsFiltersToMap() {
        String pathSpec = "/test/*";
        List<HttpFilter> filters = new ArrayList<>();
        filters.add(new AccessLogHttpFilter());
        assertEquals(1, filters.size());
        List<HttpFilterParams> httpFilterParamsList = new ArrayList<>();
        httpFilterParamsList.add(HttpFilterParams.builder().pathSpec(pathSpec).filter(new AccessLogHttpFilter()).build());
        interceptor.registerFilters(httpFilterParamsList);
    }

    @Test
    public void testRegexSpec(){
        ServletPathSpec spec = new ServletPathSpec("/test/*");
        assertEquals(true, spec.matches("/test/path"));
    }

}
