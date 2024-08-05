package com.flipkart.gjex.core.context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class AccessLogContextTest {

    @Before
    public void setUp() {
    }

    private String template = "Formatted {clientIp} {resourcePath} {contentLength} {responseTime} {headers.x-header}";

    @Test
    public void testSimpleRender() {
        AccessLogContext context = new AccessLogContext("127.0.0.1", "/path", 100, 1000L, Map.of("x-header", "value"));
        String result = context.format(template);
        Assert.assertEquals("Formatted 127.0.0.1 /path 100 1000 value", result);
    }
}
