#!/usr/bin/env bash
# Stop simulators/emulators started during a test run. Safe to run manually.
set -euo pipefail

PLATFORM="${1:-ios}"
DEVICE="${2:-}"

if [[ "$PLATFORM" == "android" ]]; then
  export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
  export PATH="$ANDROID_HOME/platform-tools:$PATH"

  if [[ -n "$DEVICE" && "$DEVICE" == emulator-* ]]; then
    adb -s "$DEVICE" emu kill 2>/dev/null || true
    echo "[mobtest] Stopped emulator: $DEVICE"
  else
    adb devices | awk '/emulator-/{print $1}' | while read -r serial; do
      adb -s "$serial" emu kill 2>/dev/null || true
      echo "[mobtest] Stopped emulator: $serial"
    done
  fi
else
  if [[ -n "$DEVICE" ]]; then
    xcrun simctl shutdown "$DEVICE" 2>/dev/null || true
    echo "[mobtest] Shut down simulator: $DEVICE"
  else
    xcrun simctl shutdown booted 2>/dev/null || true
    echo "[mobtest] Shut down booted iOS simulators."
  fi
fi
