#!/usr/bin/env bash
# Install and verify dependencies for the MobTest agentic mobile framework.
# See docs/SETUP.md for full platform setup (Xcode, Android SDK, etc.).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

echo "==> Checking Java 17+"
if ! java -version 2>&1 | grep -qE 'version "1[7-9]|version "[2-9]'; then
  echo "Install JDK 17+: https://adoptium.net/temurin/releases/?version=17"
  exit 1
fi
java -version 2>&1 | head -1

echo "==> Checking Maven 3.9+"
if ! command -v mvn >/dev/null 2>&1; then
  echo "Install Maven: https://maven.apache.org/download.cgi"
  exit 1
fi
mvn -version | head -1

echo "==> Checking Node.js 20+"
if ! command -v node >/dev/null 2>&1; then
  echo "Install Node.js 20+: https://nodejs.org/"
  exit 1
fi
node -v

echo "==> Installing Appium 2.11.5 + drivers (verified versions)"
if ! command -v appium >/dev/null 2>&1; then
  npm install -g appium@2.11.5
fi
appium -v
appium driver install xcuitest@7.28.3 2>/dev/null || appium driver update xcuitest@7.28.3 2>/dev/null || true
appium driver install uiautomator2@3.9.5 2>/dev/null || appium driver update uiautomator2@3.9.5 2>/dev/null || true

echo "==> Checking Ollama (local LLM)"
if ! command -v ollama >/dev/null 2>&1; then
  echo "Install Ollama: https://ollama.com/download"
  echo "  macOS: brew install ollama"
  echo "  Linux: curl -fsSL https://ollama.com/install.sh | sh"
  exit 1
fi
ollama -v 2>/dev/null || ollama version 2>/dev/null || true
echo "Pulling llama3.1 model (one-time, ~4.7 GB)..."
ollama pull llama3.1 || true

echo ""
echo "Setup complete. Next steps:"
echo "  1. ollama serve &"
echo "  2. appium &"
echo "  3. ./scripts/build-demo-app.sh ios    # or: android"
echo "  4. ./scripts/run-tests.sh ios  # build, test, report, cleanup"
echo ""
echo "Full guide: docs/SETUP.md"
