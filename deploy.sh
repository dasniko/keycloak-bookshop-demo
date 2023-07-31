#!/bin/bash
mvn clean package
docker build -t dasniko/bookshop -f docker/Dockerfile .
