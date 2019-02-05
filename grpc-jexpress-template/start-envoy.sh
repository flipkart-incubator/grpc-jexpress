#!/usr/bin/env bash

sudo docker run -it --rm --name envoy -p 51051:51051 -p 9901:9901 -v "$(pwd)/sample_proto_descriptor_set.pb:/tmp/sample_proto_descriptor_set.pb:ro" -v "$(pwd)/envoy.yml:/etc/envoy/envoy.yaml:ro" envoyproxy/envoy
