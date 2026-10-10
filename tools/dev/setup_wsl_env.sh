#!/usr/bin/env bash
# PlayIT: set up a local build and test environment in WSL (no sudo, no apt).
# Installs into ~/.playit-env:
#   jdk/          Temurin JDK 17 (the project targets Java 17)
#   android-sdk/  cmdline-tools, platforms;android-34, build-tools;34.0.0
# Then run tests with tools/dev/gradlew_wsl.sh (it sets JAVA_HOME and ANDROID_HOME).
# Safe to re-run: each step is skipped if it is already done.
set -euo pipefail

ENV_DIR="${PLAYIT_ENV:-$HOME/.playit-env}"
JDK_URL="https://api.adoptium.net/v3/binary/latest/17/ga/linux/x64/jdk/hotspot/normal/eclipse"
CMDLINE_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"
mkdir -p "$ENV_DIR"
cd "$ENV_DIR"

if [ ! -x jdk/bin/java ]; then
  echo "[1/3] Downloading Temurin JDK 17"
  curl -fL --retry 3 -o jdk.tar.gz "$JDK_URL"
  mkdir -p jdk && tar -xzf jdk.tar.gz -C jdk --strip-components=1 && rm jdk.tar.gz
fi
export JAVA_HOME="$ENV_DIR/jdk"
"$JAVA_HOME/bin/java" -version 2>&1 | head -1

if [ ! -x android-sdk/cmdline-tools/latest/bin/sdkmanager ]; then
  echo "[2/3] Downloading Android command-line tools"
  command -v unzip >/dev/null || { echo "unzip missing; using python zipfile"; }
  curl -fL --retry 3 -o cmdline.zip "$CMDLINE_URL"
  mkdir -p android-sdk/cmdline-tools
  if command -v unzip >/dev/null; then unzip -q cmdline.zip -d android-sdk/cmdline-tools
  else python3 -c "import zipfile,sys; zipfile.ZipFile('cmdline.zip').extractall('android-sdk/cmdline-tools')"; fi
  mv android-sdk/cmdline-tools/cmdline-tools android-sdk/cmdline-tools/latest
  chmod +x android-sdk/cmdline-tools/latest/bin/*
  rm cmdline.zip
fi
export ANDROID_HOME="$ENV_DIR/android-sdk"
SDKM="$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager"

echo "[3/3] Installing SDK packages (licenses accepted non-interactively)"
yes | "$SDKM" --sdk_root="$ANDROID_HOME" --licenses >/dev/null
"$SDKM" --sdk_root="$ANDROID_HOME" "platforms;android-34" "build-tools;34.0.0"

echo "Done. JAVA_HOME=$JAVA_HOME ANDROID_HOME=$ANDROID_HOME"
