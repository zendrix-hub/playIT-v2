#!/usr/bin/env bash
# Runs the project's Gradle wrapper with the environment from tools/dev/setup_wsl_env.sh.
# Usage (from the repo root):  tools/dev/gradlew_wsl.sh testDebugUnitTest
set -euo pipefail
ENV_DIR="${PLAYIT_ENV:-$HOME/.playit-env}"
export JAVA_HOME="$ENV_DIR/jdk"
export ANDROID_HOME="$ENV_DIR/android-sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export GRADLE_USER_HOME="${GRADLE_USER_HOME:-$ENV_DIR/gradle-home}"
cd "$(dirname "$0")/../.."
exec bash ./gradlew --no-daemon "$@"
