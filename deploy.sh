#!/bin/bash
IMAGE=ghcr.io/dasniko/keycloak-bookshop-demo
TAG=0.0.10
mvn clean package
docker build --pull -t ${IMAGE}:${TAG} -f docker/Dockerfile .
#docker buildx build --pull --platform linux/amd64,linux/arm64 -t ${IMAGE}:${TAG} -f docker/Dockerfile . --push
docker image prune -f
