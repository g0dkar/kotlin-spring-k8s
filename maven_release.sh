#!/usr/bin/env bash
set -Euxo pipefail

echo "Updating Project Version..."
PROJECT_VERSION=$(./mvnw -U org.apache.maven.plugins:maven-help-plugin:2.2:evaluate -Dexpression=project.version | grep '^[[:digit:]].[[:digit:]].[[:digit:]]\$')
GIT_COMMIT_VERSION=$(git log --pretty=format:'%h' -n 1)
NEW_PROJECT_VERSION=${PROJECT_VERSION}-${BUILD_ID}-${GIT_COMMIT_VERSION}
./mvnw versions:set -DnewVersion=${NEW_PROJECT_VERSION}

#./mvnw deploy -X -DupdateReleaseInfo=true -DdeployAtEnd=true
./maven_build.sh
