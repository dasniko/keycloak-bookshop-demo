#!/bin/bash
IMAGE=dasniko/bookshop
TAG=0.0.7
mvn clean package
docker build -t ${IMAGE}:${TAG} -f docker/Dockerfile .
#docker buildx build --pull --platform linux/amd64,linux/arm64 -t ${IMAGE}:${TAG} -f docker/Dockerfile . --push
docker image prune -f
