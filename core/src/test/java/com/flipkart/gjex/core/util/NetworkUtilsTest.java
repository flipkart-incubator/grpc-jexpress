package com.flipkart.gjex.core.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NetworkUtilsTest {


    @Test
    public void extractIPAddressReturnsIPv4Address() {
        String input = "User IP is 192.168.1.1 and should be extracted";
        String result = NetworkUtils.extractIPAddress(input);
        assertEquals("192.168.1.1", result);
    }

    @Test
    public void extractIPAddressReturnsIPv6Address() {
        String input = "User IP is 2001:0db8:85a3:0000:0000:8a2e:0370:7334 and should be extracted";
        String result = NetworkUtils.extractIPAddress(input);
        assertEquals("2001:0db8:85a3:0000:0000:8a2e:0370:7334", result);
    }

    @Test
    public void extractIPAddressReturnsFirstIPv4AddressWhenMultiplePresent() {
        String input = "User IPs are 192.168.1.1 and 10.0.0.1, first one should be extracted";
        String result = NetworkUtils.extractIPAddress(input);
        assertEquals("192.168.1.1", result);
    }

    @Test
    public void extractIPAddressReturnsWithPrefix() {
        String input = "/192.168.1.1:1234";
        String result = NetworkUtils.extractIPAddress(input);
        assertEquals("192.168.1.1", result);
    }

    @Test
    public void extractIPAddressReturnsFirstIPv6AddressWhenMultiplePresent() {
        String input = "User IPs are 2001:0db8:85a3:0000:0000:8a2e:0370:7334 and fe80::1ff:fe23:4567:890a, first one should be extracted";
        String result = NetworkUtils.extractIPAddress(input);
        assertEquals("2001:0db8:85a3:0000:0000:8a2e:0370:7334", result);
    }

    @Test
    public void extractIPFromStringReturnsDefaultWhenNoIPPresent() {
        String input = "No IP address in this string";
        String result = NetworkUtils.extractIPAddress(input);
        assertEquals("0.0.0.0", result);
    }

    @Test
    public void extractIPAddress() {
        String input = "";
        String result = NetworkUtils.extractIPAddress(input);
        assertEquals("0.0.0.0", result);
    }

    @Test
    public void extractIPAddressHandlesNullInput() {
        String result = NetworkUtils.extractIPAddress(null);
        assertEquals("0.0.0.0", result);
    }
}
