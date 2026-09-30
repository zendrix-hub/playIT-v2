#!/usr/bin/env bash
# Runs the project's Gradle wrapper with the environment from tools/dev/setup_wsl_env.sh.
# Usage (from the repo root):  tools/dev/gradlew_wsl.sh testDebugUnitTest
set -euo pipefail
ENV_DIR="${PLAYIT_ENV:-$HOME/.playit-env}"
export JAVA_HOME="$ENV_DIR/jdk"
export ANDROID_HOME="$ENV_DIR/android-sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export GRADLE_USER_HOME="${GRADLE_USER_HOME:-$ENV_DIR/gradle-home}"
# Java ignores HTTPS_PROXY; pass it on as JVM system properties (covers the wrapper download,
# the build, and forked workers) when the shell sits behind a proxy.
PROXY="${HTTPS_PROXY:-${https_proxy:-}}"
if [ -n "$PROXY" ]; then
  hostport="${PROXY#*://}"; hostport="${hostport#*@}"; hostport="${hostport%%/*}"
  host="${hostport%:*}"; port="${hostport##*:}"
  props="-Dhttp.proxyHost=$host -Dhttp.proxyPort=$port -Dhttps.proxyHost=$host -Dhttps.proxyPort=$port"
  export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} $props -Dhttp.nonProxyHosts=localhost|127.0.0.1"
fi
cd "$(dirname "$0")/../.."
exec bash ./gradlew --no-daemon "$@"
