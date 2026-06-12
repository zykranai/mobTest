#!/usr/bin/env bash
# Build the bundled ShopMate demo apps into apps/*/dist/ for first-run tests.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PLATFORM="${1:-ios}"

if [[ "$PLATFORM" == "android" ]]; then
  export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
  export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
  cd "$ROOT/apps/shopmate-android"
  ./gradlew assembleDebug --no-daemon
  mkdir -p dist
  cp app/build/outputs/apk/debug/app-debug.apk dist/shopmate-debug.apk
  echo "Built: $ROOT/apps/shopmate-android/dist/shopmate-debug.apk"
else
  DEVICE="${IOS_DEVICE:-iPhone 17}"
  cd "$ROOT/apps/shopmate-ios"
  xcodebuild -project ShopMate.xcodeproj -scheme ShopMate \
    -destination "platform=iOS Simulator,name=$DEVICE" \
    -derivedDataPath build CODE_SIGNING_ALLOWED=NO build
  mkdir -p dist
  rm -rf dist/ShopMate.app
  cp -R build/Build/Products/Debug-iphonesimulator/ShopMate.app dist/
  echo "Built: $ROOT/apps/shopmate-ios/dist/ShopMate.app"
fi
