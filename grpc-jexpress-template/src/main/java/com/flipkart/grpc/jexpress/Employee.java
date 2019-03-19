package com.flipkart.grpc.jexpress;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Employee {

    private String name;

    private int age;

    private List<String> toys;

    Map<String, Object> properties;

}
