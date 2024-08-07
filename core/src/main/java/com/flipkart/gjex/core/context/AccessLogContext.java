package com.flipkart.gjex.core.context;


import lombok.Builder;
import lombok.SneakyThrows;
import org.apache.commons.text.StringSubstitutor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the context for access logs, encapsulating details such as client IP, resource path,
 * content length, response time, response status, and headers. This class provides functionality
 * to format these details into a string using a template.
 */
@Builder
public class AccessLogContext {
    private String clientIp;
    private String resourcePath;
    private Integer contentLength;
    private Long responseTime;
    private Integer responseStatus;
    @Builder.Default
    private Map<String,String> headers = new HashMap<>();
    @Builder.Default
    protected Map<String, String> customFields = new HashMap<>();


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
        for (Map.Entry<String, String> entry : customFields.entrySet()) {
            params.put("custom." + entry.getKey(), entry.getValue());
        }
        params.put("thread", Thread.currentThread().getName());
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
