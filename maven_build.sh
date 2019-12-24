#!/usr/bin/env bash
set -Euxo pipefail

echo "Building Maven Artifacts..."

./mvnw clean dependency-check:check test verify package dokka:dokka -DfinalName=app --projects client,service
