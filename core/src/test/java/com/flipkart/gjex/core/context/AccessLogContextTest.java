package com.flipkart.gjex.core.context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

public class AccessLogContextTest {

    @Before
    public void setUp() {
    }


    @Test
    public void testSimpleRender() {
        String template = "Formatted {clientIp} {resourcePath} {contentLength} {responseTime} {headers.x-header}";
        AccessLogContext context =
            AccessLogContext.builder()
                .clientIp("127.0.0.1")
                .resourcePath("/path")
                .responseTime(1000L)
                .contentLength(100)
                .headers(Collections.singletonMap("x-header", "value"))
                .build();
        String result = context.format(template);
        Assert.assertEquals("Formatted 127.0.0.1 /path 100 1000 value", result);
    }

    @Test
    public void testRenderUserContext() {
        String template = "Formatted {clientIp} - {user} {resourcePath} {contentLength} {responseTime} {headers.x-header}";
        AccessLogContext context =
            AccessLogContext.builder()
                .clientIp("127.0.0.1")
                .resourcePath("/path")
                .responseTime(1000L)
                .contentLength(100)
                .headers(Collections.singletonMap("x-header", "value"))
                .userContext(() -> Collections.singletonMap("user", "username"))
                .build();
        String result = context.format(template);
        Assert.assertEquals("Formatted 127.0.0.1 - username /path 100 1000 value", result);
    }
}
