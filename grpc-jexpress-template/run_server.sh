#!/usr/bin/env bash

java -cp target/grpc-jexpress-template-1.0-SNAPSHOT.jar:target/lib/* com.flipkart.grpc.jexpress.SampleApplication -Dgjex.configurationFile=src/main/resources/configuration.yml