package com.flipkart.gjex.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkUtils {

    private static final Pattern[] ipAddressPattern = {
        Pattern.compile( "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})"),
        Pattern.compile("((([0-9a-fA-F]){1,4})\\:){7}([0-9a-fA-F]){1,4}")
    };


    /**
     * Extracts the IP address from a given string.
     *
     * @param str The input string from which the IP address is to be extracted.
     * @return The IP address extracted from the input string.
     */
    public static String extractIPAddress(String str) {
        if (str != null) {
            for (Pattern pattern : ipAddressPattern) {
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    return matcher.group();
                }
            }
        }
        return "0.0.0.0";
    }
}
