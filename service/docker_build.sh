#!/usr/bin/env bash
set -e

echo "Bulding Project..."
mvn clean package -Dfinalname=app

echo "Building Image..."
IMAGE=pipeleap-transactions
TAG=`git log --format="%H" -n 1 | cut -c1-6`

docker build -t ${IMAGE}:${TAG} \
             -t ${IMAGE}:latest .
