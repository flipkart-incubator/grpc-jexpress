#!/usr/bin/env bash

# envoy doesn't support a latest tag anymore, so find the latest version and use it accordingly
sudo docker run -it --rm --name envoy -p 51051:51051 -p 9901:9901 -v "$(pwd)/sample_proto_descriptor_set.pb:/tmp/sample_proto_descriptor_set.pb:ro" -v "$(pwd)/envoy.yml:/etc/envoy/envoy.yaml:ro" envoyproxy/envoy:v1.31-latest
