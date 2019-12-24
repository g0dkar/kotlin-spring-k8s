#!/usr/bin/env bash
set -e

echo "Bulding Project..."
mvn clean dependency-check:check package -DfinalName=app -DskipTests

echo "Building Image..."
IMAGE=sample-k8s-service
TAG=`git log --format="%H" -n 1 | cut -c1-6`

docker build -t ${IMAGE}:${TAG} \
             -t ${IMAGE}:latest .

rm -rf target/app.jar
