package com.flipkart.gjex.grpc.interceptor;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static org.junit.Assert.assertEquals;

public class FilterInterceptorTest {

    @Test
    public void getClientIpReturnsHostNameForInetSocketAddress() {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 1234);
        String result = FilterInterceptor.getClientIp(inetSocketAddress);
        assertEquals("localhost", result);
    }

    @Test
    public void getClientIpReturnsExtractedIpForNonInetSocketAddress() {
        SocketAddress socketAddress = new SocketAddress() {
            @Override
            public String toString() {
                return "192.168.1.1:1234";
            }
        };
        String result = FilterInterceptor.getClientIp(socketAddress);
        assertEquals("192.168.1.1", result);
    }

    @Test
    public void getClientIpReturnsDefaultIpForNullSocketAddress() {
        String result = FilterInterceptor.getClientIp(null);
        assertEquals("0.0.0.0", result);
    }

    @Test
    public void getClientIpReturnsExtractedIpForGRPCSocketAddress() {
        SocketAddress socketAddress = new SocketAddress() {
            @Override
            public String toString() {
                return "/192.168.1.1:1234";
            }
        };
        String result = FilterInterceptor.getClientIp(socketAddress);
        assertEquals("192.168.1.1", result);
    }

    @Test
    public void getClientIpReturnsExtractedFirstIpForGRPCSocketAddress() {
        SocketAddress socketAddress = new SocketAddress() {
            @Override
            public String toString() {
                return "192.168.1.1/192.168.1.2:1234";
            }
        };
        String result = FilterInterceptor.getClientIp(socketAddress);
        assertEquals("192.168.1.1", result);
    }


}
