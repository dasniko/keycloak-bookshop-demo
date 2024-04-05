#!/bin/bash
mvn clean package
docker build -t dasniko/bookshop:0.0.5 -f docker/Dockerfile .
#docker buildx build --pull --platform linux/amd64,linux/arm64 -t dasniko/bookshop:0.0.5 -f docker/Dockerfile . --push
docker image prune -f
