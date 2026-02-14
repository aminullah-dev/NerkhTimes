#!/usr/bin/env sh
# Minimal Gradle wrapper script (works on macOS/Linux)
DIR="$(cd "$(dirname "$0")" && pwd)"
exec java -jar "$DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
