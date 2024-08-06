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
import static org.junit.Assert.assertTrue;


public class HttpFilterInterceptorTest {

    private HttpFilterInterceptor interceptor;
    String pathSpec = "/test/*";

    @Before
    public void setUp() {
        interceptor = new HttpFilterInterceptor();
    }

    @Test
    public void registerFiltersAddsFiltersToMap() {
        List<HttpFilterParams> httpFilterParamsList = new ArrayList<>();
        httpFilterParamsList.add(HttpFilterParams.builder().pathSpec(pathSpec).filter(new AccessLogHttpFilter()).build());
        interceptor.registerFilters(httpFilterParamsList);
        assertEquals(1, interceptor.getMatchingFilters("/test/path").size());
    }

    @Test
    public void testRegexSpec(){
        ServletPathSpec spec = new ServletPathSpec("/test/*");
        assertTrue(spec.matches("/test/path"));
    }


    @Test
    public void registerFiltersAddsFiltersToMap2() {
        List<HttpFilterParams> httpFilterParamsList = new ArrayList<>();
        httpFilterParamsList.add(HttpFilterParams.builder().pathSpec(pathSpec).filter(new AccessLogHttpFilter()).build());
        httpFilterParamsList.add(HttpFilterParams.builder().pathSpec(pathSpec).filter(new AccessLogHttpFilter()).build());
        interceptor.registerFilters(httpFilterParamsList);
        assertEquals(2, interceptor.getMatchingFilters("/test/path").size());
    }


}
