#!/usr/bin/env bash
#set -x
mvn clean install
rm -rf *.pb
proto_dependencies=$(ls -1 target/protoc-dependencies/*|grep target|cut -d":" -f1|sed s/^/-I/g|tr '\n' ' ')
protoc -I.  ${proto_dependencies} -Isrc/main/proto --include_imports  --include_source_info  --descriptor_set_out=sample_proto_descriptor_set.pb  src/main/proto/userservice.proto
