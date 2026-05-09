#!/bin/sh
GRADLE_VERSION=8.9
GRADLE_HOME="${HOME}/.gradle/wrapper/dists/gradle-${GRADLE_VERSION}-bin"
GRADLE_CMD="${GRADLE_HOME}/gradle-${GRADLE_VERSION}/bin/gradle"

if [ ! -f "$GRADLE_CMD" ]; then
    mkdir -p "$GRADLE_HOME"
    cd "$GRADLE_HOME"
    curl -L "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o gradle.zip
    unzip -q gradle.zip
    rm gradle.zip
    cd -
fi

exec "$GRADLE_CMD" "$@"
