package com.flipkart.gjex.core.context;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.text.StringSubstitutor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Builder
public class AccessLogContext {
    String clientIp;
    String resourcePath;
    Integer contentLength;
    Long responseTime;
    Map<String,String> headers;

    @SneakyThrows
    Map<String,Object> getValueMap() {
        Map<String, Object> params = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            params.put(field.getName(), field.get(this));
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            params.put("headers." + entry.getKey(), entry.getValue());
        }
        return params;
    }

    public String format(String templateFormat) {
        return StringSubstitutor.replace(templateFormat, getValueMap(), "{", "}");
    }
}
