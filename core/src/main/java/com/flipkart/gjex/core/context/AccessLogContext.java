package com.flipkart.gjex.core.context;


import lombok.Builder;
import lombok.SneakyThrows;
import org.apache.commons.text.StringSubstitutor;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Represents the context for access logs, encapsulating details such as client IP, resource path,
 * content length, response time, response status, and headers. This class provides functionality
 * to format these details into a string using a template.
 */
@Builder
public class AccessLogContext {
    String clientIp;
    String resourcePath;
    Integer contentLength;
    Long responseTime;
    Integer responseStatus;
    String method;
    String protocol;
    Long requestTime;
    Supplier<Map<String,String>> userContext;

    @Builder.Default
    String referer = "-";
    @Builder.Default
    String userAgent = "";
    @Builder.Default
    String user = "";
    @Builder.Default
    Map<String,String> headers = new HashMap<>();

    /**
     * Retrieves a map of field names to their values for the current instance.
     * This includes the fields of the class and the headers map, as well as the current thread name.
     *
     * @return A map containing field names and their corresponding values.
     * @throws IllegalAccessException if the field is not accessible.
     */
    @SneakyThrows
    private Map<String,Object> getValueMap() {
        Map<String, Object> params = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            params.put(field.getName(), field.get(this));
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            params.put("headers." + entry.getKey(), entry.getValue());
        }

        if (requestTime != null) {
            String localDate = OffsetDateTime.ofInstant(Instant.ofEpochMilli(requestTime), ZoneId.systemDefault()).format(DateTimeFormatter.ISO_DATE_TIME);
            params.put("requestTime", localDate);
        }
        params.put("thread", Thread.currentThread().getName());
        if (userContext != null)
            params.putAll(userContext.get());
        return params;
    }

    /**
     * Formats the access log context into a string based on the provided template.
     * The template can include placeholders for the field names, which will be replaced
     * with their corresponding values.
     *
     * @param templateFormat The template string containing placeholders for field names.
     * @return The formatted string with placeholders replaced by field values.
     */
    public String format(final String templateFormat) {
        return StringSubstitutor.replace(templateFormat, getValueMap(), "{", "}");
    }
}
