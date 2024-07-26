#!/usr/bin/env bash

java -cp target/grpc-jexpress-template-1.0-SNAPSHOT.jar:target/lib/* --add-opens=java.base/java.lang=ALL-UNNAMED com.flipkart.grpc.jexpress.SampleApplication server src/main/resources/configuration.yml
