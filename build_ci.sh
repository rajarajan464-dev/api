#!/bin/bash
set -e

# Workaround for CI
export GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs=\"-Xmx4096m -XX:MaxMetaspaceSize=1024m\""

echo "=== Building Android App Bundle (AAB) ==="
gradle :app:bundleRelease --no-daemon --stacktrace

echo "=== Building Debug APK ==="
gradle :app:assembleDebug --no-daemon --stacktrace

echo "=== BUILD COMPLETE SUCCESS ==="
