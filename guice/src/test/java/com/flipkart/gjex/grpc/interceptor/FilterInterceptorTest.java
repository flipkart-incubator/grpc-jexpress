package com.flipkart.gjex.grpc.interceptor;

import org.junit.Assert;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class FilterInterceptorTest {
    private static final String IP_ADDRESS = "1.2.3.4";

    @Test
    public void getClientIpTest(){
        InetSocketAddress inetSocketAddress = new InetSocketAddress(IP_ADDRESS, 1234);
        Assert.assertEquals(IP_ADDRESS, FilterInterceptor.getClientIp(inetSocketAddress));

        SocketAddress socketAddress = new SocketAddress() {
            @Override
            public String toString() {
                return IP_ADDRESS;
            }
        };
        Assert.assertEquals(IP_ADDRESS, FilterInterceptor.getClientIp(socketAddress));
    }

}
