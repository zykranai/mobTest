#!/usr/bin/env bash
# Stable end-to-end workflow: optional build → boot device → services → test → report → cleanup.
#
# Usage:
#   ./scripts/run-tests.sh ios
#   ./scripts/run-tests.sh android
#   ./scripts/run-tests.sh ios --no-build
#   ./scripts/run-tests.sh android --heuristic
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PLATFORM="ios"
NO_BUILD=false
HEURISTIC=false
IOS_DEVICE="${IOS_DEVICE:-iPhone 17}"
ANDROID_DEVICE="${ANDROID_DEVICE:-emulator-5554}"
ANDROID_AVD="${ANDROID_AVD:-}"

while [[ $# -gt 0 ]]; do
  case "$1" in
    ios|android) PLATFORM="$1" ;;
    --no-build) NO_BUILD=true ;;
    --heuristic) HEURISTIC=true ;;
    -h|--help)
      echo "Usage: $0 [ios|android] [--no-build] [--heuristic]"
      exit 0
      ;;
    *) echo "Unknown option: $1"; exit 1 ;;
  esac
  shift
done

cleanup_devices() {
  "$ROOT/scripts/cleanup-devices.sh" "$PLATFORM" \
    "$( [[ "$PLATFORM" == android ]] && echo "$ANDROID_DEVICE" || echo "$IOS_DEVICE" )" \
    2>/dev/null || true
}
trap cleanup_devices EXIT

dist_ready() {
  if [[ "$PLATFORM" == android ]]; then
    [[ -f "$ROOT/apps/shopmate-android/dist/shopmate-debug.apk" ]]
  else
    [[ -d "$ROOT/apps/shopmate-ios/dist/ShopMate.app" ]] \
      && [[ -f "$ROOT/apps/shopmate-ios/dist/ShopMate.app/Info.plist" ]]
  fi
}

if [[ "$NO_BUILD" == "false" ]]; then
  if dist_ready; then
    echo "[mobtest] Using existing demo app in apps/*/dist/ (pass --no-build to skip this message)"
  else
    echo "[mobtest] Building demo app..."
    "$ROOT/scripts/build-demo-app.sh" "$PLATFORM"
  fi
elif ! dist_ready; then
  echo "Demo app missing in apps/*/dist/. Run without --no-build first."
  exit 1
fi

if [[ "$PLATFORM" == "android" ]]; then
  export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
  export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"

  if ! adb devices | grep -qE 'emulator-[0-9]+\s+device'; then
    if [[ -n "$ANDROID_AVD" ]]; then
      echo "[mobtest] Starting emulator: $ANDROID_AVD"
      emulator -avd "$ANDROID_AVD" -no-snapshot-load >/tmp/android-emulator.log 2>&1 &
      adb wait-for-device
      sleep 5
    else
      echo "No Android emulator running. Start one or set ANDROID_AVD=Your_Avd_Name"
      exit 1
    fi
  fi

  SUITE="suites/android.xml"
  MVN_ARGS=(-Dplatform=android -DsuiteXmlFile="$SUITE" -Dandroid.device.name="$ANDROID_DEVICE")
else
  xcrun simctl boot "$IOS_DEVICE" 2>/dev/null || true
  open -a Simulator 2>/dev/null || true
  SUITE="suites/ios.xml"
  MVN_ARGS=(-Dplatform=ios -DsuiteXmlFile="$SUITE" -Dios.device.name="$IOS_DEVICE")
fi

# Tell Java teardown which device the script booted.
MVN_ARGS+=(-Ddevice.managed.by.framework=true)

if ! curl -sf http://127.0.0.1:11434/api/tags >/dev/null 2>&1; then
  echo "[mobtest] Starting Ollama..."
  (ollama serve >/tmp/ollama.log 2>&1 &) \
    || (/Applications/Ollama.app/Contents/Resources/ollama serve >/tmp/ollama.log 2>&1 &)
  sleep 3
fi

if ! curl -sf http://127.0.0.1:4723/status >/dev/null 2>&1; then
  echo "[mobtest] Starting Appium..."
  appium >/tmp/appium.log 2>&1 &
  sleep 5
fi

cd "$ROOT"
if [[ "$HEURISTIC" == "true" ]]; then
  mvn clean test "${MVN_ARGS[@]}" -Dagent.heuristic.only=true
else
  mvn clean test "${MVN_ARGS[@]}"
fi

TEST_EXIT=$?
"$ROOT/scripts/generate-report.sh" || true
exit $TEST_EXIT
