#!/usr/bin/env bash
# Installs dependencies for the agentic mobile test framework.
# Prerequisites you install manually: Xcode (iOS), Android Studio/SDK (Android), Node.js 20+.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

echo "==> Checking Java 17+"
if ! java -version 2>&1 | grep -q 'version "1[7-9]\|version "[2-9]'; then
  echo "Install JDK 17+: https://adoptium.net/"
  exit 1
fi

echo "==> Checking Maven"
if ! command -v mvn >/dev/null 2>&1; then
  echo "Install Maven: https://maven.apache.org/download.cgi"
  exit 1
fi

echo "==> Checking Node.js"
if ! command -v node >/dev/null 2>&1; then
  echo "Install Node.js 20+: https://nodejs.org/"
  exit 1
fi

echo "==> Installing Appium 2 + drivers"
if ! command -v appium >/dev/null 2>&1; then
  npm install -g appium@2.11.5
fi
appium driver install xcuitest@7.28.3 2>/dev/null || true
appium driver install uiautomator2@3.9.5 2>/dev/null || true

echo "==> Checking Ollama (local LLM)"
if ! command -v ollama >/dev/null 2>&1; then
  echo "Install Ollama: https://ollama.com/download"
  echo "Then run: ollama pull llama3.1"
else
  echo "Pulling llama3.1 model (one-time, ~4.7GB)..."
  ollama pull llama3.1 || true
fi

echo ""
echo "Setup complete. Next steps:"
echo "  1. ollama serve &"
echo "  2. ./scripts/build-sample-app.sh ios    # or android"
echo "  3. appium &"
echo "  4. mvn test                             # iOS default"
echo "  5. mvn test -Dplatform=android -DsuiteXmlFile=testng-android.xml"
