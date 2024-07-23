package com.flipkart.grpc.jexpress;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class Database {

    @NotNull
    private String driverClass;

    private String user;
    private String password;

    private String url;

    @Min(5)
    private int initialSize;

    @Min(5)
    private int minSize;

    @Max(30)
    private int maxSize;

    private boolean checkConnectionWhileIdle;

    private Map<String, String> properties = new LinkedHashMap<>();

}
