#!/usr/bin/env bash
set -e

echo "Bulding Project..."
mvn clean package -DfinalName=app

echo "Building Image..."
IMAGE=sample-k8s-service
TAG=`git log --format="%H" -n 1 | cut -c1-6`

docker build -t ${IMAGE}:${TAG} \
             -t ${IMAGE}:latest .