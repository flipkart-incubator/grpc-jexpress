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

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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


    @Test
    public void getFullURLReturnsURLWithoutQueryString() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com/test"));
        when(request.getQueryString()).thenReturn(null);

        String result = HttpFilterInterceptor.getFullURL(request);

        assertEquals("http://example.com/test", result);
    }

    @Test
    public void getFullURLReturnsURLWithQueryString() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com/test"));
        when(request.getQueryString()).thenReturn("param1=value1&param2=value2");

        String result = HttpFilterInterceptor.getFullURL(request);

        assertEquals("http://example.com/test?param1=value1&param2=value2", result);
    }

    @Test
    public void getFullURLHandlesEmptyQueryString() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com/test"));
        when(request.getQueryString()).thenReturn("");

        String result = HttpFilterInterceptor.getFullURL(request);

        assertEquals("http://example.com/test?", result);
    }

}
