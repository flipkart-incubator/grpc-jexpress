#!/usr/bin/env bash
curl -XPOST "http://localhost:51051/v1/userservice" -H 'Content-Type: application/json' -d  '{ "userName": "Foo"}'


curl "http://localhost:51051/v1/userservice/1" -H 'Content-Type: application/json'
