#!/usr/bin/env bash

exec java -cp target/grpc-jexpress-template-1.0-SNAPSHOT.jar:target/lib/* com.flipkart.grpc.jexpress.SampleClient
