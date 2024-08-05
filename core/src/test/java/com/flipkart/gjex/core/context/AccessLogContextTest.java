package com.flipkart.gjex.core.context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

public class AccessLogContextTest {

    @Before
    public void setUp() {
    }

    private String template = "Formatted {clientIp} {resourcePath} {contentLength} {responseTime} {headers.x-header}";

    @Test
    public void testSimpleRender() {
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
}
