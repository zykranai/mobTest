#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PLATFORM="${1:-ios}"

if [[ "$PLATFORM" == "android" ]]; then
  export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
  export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
  cd "$ROOT/sample-apps/android"
  ./gradlew assembleDebug --no-daemon
  echo "Built: $ROOT/sample-apps/android/app/build/outputs/apk/debug/app-debug.apk"
else
  DEVICE="${IOS_DEVICE:-iPhone 17}"
  cd "$ROOT/sample-apps/ios"
  xcodebuild -project ShopMate.xcodeproj -scheme ShopMate \
    -destination "platform=iOS Simulator,name=$DEVICE" \
    -derivedDataPath build CODE_SIGNING_ALLOWED=NO build
  echo "Built: $ROOT/sample-apps/ios/build/Build/Products/Debug-iphonesimulator/ShopMate.app"
fi
