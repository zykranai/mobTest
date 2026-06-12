#!/usr/bin/env bash
# Build ShopMate demo apps into apps/*/dist/
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PLATFORM="${1:-ios}"

if [[ "$PLATFORM" == "android" ]]; then
  export JAVA_HOME="${JAVA_HOME:-/Applications/Android Studio.app/Contents/jbr/Contents/Home}"
  export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
  export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
  cd "$ROOT/apps/shopmate-android"
  ./gradlew assembleDebug --no-daemon
  mkdir -p dist
  cp app/build/outputs/apk/debug/app-debug.apk dist/shopmate-debug.apk
  OUT="$ROOT/apps/shopmate-android/dist/shopmate-debug.apk"
else
  DEVICE="${IOS_DEVICE:-iPhone 17}"
  cd "$ROOT/apps/shopmate-ios"
  xcodebuild -project ShopMate.xcodeproj -scheme ShopMate \
    -destination "platform=iOS Simulator,name=$DEVICE" \
    -derivedDataPath build CODE_SIGNING_ALLOWED=NO build
  mkdir -p dist
  rm -rf dist/ShopMate.app
  cp -R build/Build/Products/Debug-iphonesimulator/ShopMate.app dist/
  OUT="$ROOT/apps/shopmate-ios/dist/ShopMate.app"
fi

if [[ ! -e "$OUT" ]]; then
  echo "Build failed — output not found: $OUT"
  exit 1
fi
echo "[mobtest] Built: $OUT"
