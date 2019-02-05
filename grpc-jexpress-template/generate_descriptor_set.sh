#!/usr/bin/env bash
mvn clean install
rm -rf *.pb
proto_dependencies=$(ls -1 target/protoc-dependencies/*|head -n1|cut -d":" -f1)
protoc -I.  -I${proto_dependencies} -Isrc/main/proto --include_imports  --include_source_info  --descriptor_set_out=sample_proto_descriptor_set.pb  src/main/proto/userservice.proto
