#!/usr/bin/env bash
# Build demo app, start Ollama/Appium if needed, and run agentic tests.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PLATFORM="${1:-ios}"
HEURISTIC="${AGENT_HEURISTIC_ONLY:-false}"

"$ROOT/scripts/build-demo-app.sh" "$PLATFORM"

if [[ "$PLATFORM" == "android" ]]; then
  export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
  export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
  adb devices | grep -q emulator || (echo "Start an Android emulator first. See docs/SETUP.md" && exit 1)
  SUITE="suites/android.xml"
  MVN_ARGS=(-Dplatform=android -DsuiteXmlFile="$SUITE")
else
  DEVICE="${IOS_DEVICE:-iPhone 17}"
  xcrun simctl boot "$DEVICE" 2>/dev/null || true
  open -a Simulator 2>/dev/null || true
  SUITE="suites/ios.xml"
  MVN_ARGS=(-Dplatform=ios -DsuiteXmlFile="$SUITE")
fi

if ! curl -sf http://127.0.0.1:11434/api/tags >/dev/null 2>&1; then
  echo "Starting Ollama..."
  (ollama serve >/tmp/ollama.log 2>&1 &) || (/Applications/Ollama.app/Contents/Resources/ollama serve >/tmp/ollama.log 2>&1 &)
  sleep 3
fi

if ! curl -sf http://127.0.0.1:4723/status >/dev/null 2>&1; then
  echo "Starting Appium..."
  appium >/tmp/appium.log 2>&1 &
  sleep 5
fi

cd "$ROOT"
if [[ "$HEURISTIC" == "true" ]]; then
  mvn clean test "${MVN_ARGS[@]}" -Dagent.heuristic.only=true
else
  mvn clean test "${MVN_ARGS[@]}"
fi
