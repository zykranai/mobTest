# Environment Setup

Complete install guide for the **MobTest Agentic Mobile Framework**. All versions below were verified on **macOS (Apple Silicon)** with the bundled ShopMate demo apps.

---

## Verified versions

| Component | Version | Notes |
|-----------|---------|-------|
| **Java (Temurin)** | 17.0.14+ | Required for Maven and TestNG |
| **Maven** | 3.9.9+ | Build and run tests |
| **Node.js** | 20.x or 22.x | Appium runtime |
| **Appium** | 2.11.5 | Mobile automation server |
| **XCUITest driver** | 7.28.3 | iOS automation |
| **UiAutomator2 driver** | 3.9.5 | Android automation |
| **Ollama** | 0.30.7+ | Local LLM server |
| **Ollama model** | `llama3.1` | Default planning model (~4.7 GB) |
| **Xcode** | 26.5 | iOS builds and Simulator |
| **iOS Simulator** | iPhone 17, iOS 26.5 | Default in `config.properties` |
| **Android SDK** | API 36 emulator | `Medium_Phone_API_36.1` AVD |
| **Appium Java Client** | 8.6.0 | See `pom.xml` |
| **Selenium** | 4.21.0 | Transitive Appium dependency |
| **TestNG** | 7.10.2 | Test runner |
| **Allure** | 2.27.0 | Step-level reporting |

> **Note:** iOS Simulator version must match your installed Xcode runtimes. Run `xcrun simctl list devices available` and update `ios.platform.version` / `ios.device.name` in config if needed.

---

## 1. Clone the repository

```bash
git clone https://github.com/zykranai/mobTest.git
cd mobTest
```

---

## 2. Install Java 17 and Maven

### macOS (Homebrew)

```bash
brew install openjdk@17 maven
export JAVA_HOME="$(/usr/libexec/java_home -v 17)"
echo 'export JAVA_HOME="$(/usr/libexec/java_home -v 17)"' >> ~/.zshrc
```

### Linux (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk maven
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

Verify:

```bash
java -version    # should show 17.x
mvn -version     # should show Maven 3.9+
```

---

## 3. Install Node.js and Appium

### macOS

```bash
brew install node@22
npm install -g appium@2.11.5
appium driver install xcuitest@7.28.3
appium driver install uiautomator2@3.9.5
```

### Linux

```bash
curl -fsSL https://deb.nodesource.com/setup_22.x | sudo -E bash -
sudo apt install -y nodejs
sudo npm install -g appium@2.11.5
appium driver install xcuitest@7.28.3
appium driver install uiautomator2@3.9.5
```

Verify:

```bash
node -v          # v20+ or v22+
appium -v        # 2.11.5
appium driver list --installed
```

**Or use the project script** (checks versions and installs Appium + Ollama model):

```bash
chmod +x scripts/*.sh
./scripts/setup-environment.sh
```

---

## 4. Install Ollama and pull Llama 3.1

Ollama runs the local LLM that plans test actions. No API key required.

### macOS

```bash
brew install ollama
# or download from https://ollama.com/download
```

### Linux

```bash
curl -fsSL https://ollama.com/install.sh | sh
```

Pull the model and start the server:

```bash
ollama pull llama3.1
ollama serve &
```

Verify:

```bash
curl http://localhost:11434/api/tags
# Should list "llama3.1"
```

Optional: use heuristic-only mode (no LLM) for fast smoke tests:

```bash
mvn test -Dagent.heuristic.only=true
```

---

## 5. iOS setup (macOS only)

1. Install **Xcode** from the Mac App Store.
2. Open Xcode once and accept the license.
3. Install command-line tools:

```bash
xcode-select --install
```

4. List available simulators:

```bash
xcrun simctl list devices available
```

5. Boot the default simulator:

```bash
xcrun simctl boot "iPhone 17"
open -a Simulator
```

6. Build the demo app (if not using the prebuilt `dist/` bundle):

```bash
./scripts/build-demo-app.sh ios
```

---

## 6. Android setup

1. Install [Android Studio](https://developer.android.com/studio).
2. In SDK Manager, install:
   - Android SDK Platform 36 (or your target API)
   - Android SDK Build-Tools
   - Android Emulator
3. Create an AVD (e.g. **Medium Phone API 36**):

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"   # macOS default
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"

# List AVDs
emulator -list-avds

# Start emulator (replace with your AVD name)
emulator -avd Medium_Phone_API_36.1 &
adb devices    # should show emulator-5554
```

4. Build the demo APK (if not using the prebuilt `dist/` bundle):

```bash
./scripts/build-demo-app.sh android
```

---

## 7. Start services

Open two terminals (or use the run script which auto-starts them):

**Terminal 1 — Ollama**

```bash
ollama serve
```

**Terminal 2 — Appium**

```bash
appium
```

Verify:

```bash
curl http://127.0.0.1:11434/api/tags
curl http://127.0.0.1:4723/status
```

---

## 8. Run your first test

Prebuilt demo apps are included under `apps/*/dist/` so you can run immediately after setup.

### iOS

```bash
mvn test -DsuiteXmlFile=suites/ios.xml
```

### Android

```bash
mvn test -Dplatform=android -DsuiteXmlFile=suites/android.xml
```

### One-command smoke test

```bash
./scripts/run-demo-tests.sh ios
./scripts/run-demo-tests.sh android

# Fast mode (no LLM):
AGENT_HEURISTIC_ONLY=true ./scripts/run-demo-tests.sh ios
```

### View Allure report

```bash
mvn allure:serve
```

---

## Troubleshooting

| Issue | Fix |
|-------|-----|
| `Could not find simulator` | Run `xcrun simctl list devices` and update `ios.device.name` / `ios.platform.version` in `config.properties` |
| Appium connection refused | Ensure `appium` is running on port 4723 |
| Ollama timeout | Run `ollama serve` and `ollama pull llama3.1`; or use `-Dagent.heuristic.only=true` |
| Android `device offline` | Restart emulator; run `adb kill-server && adb start-server` |
| Wrong app path | Rebuild with `./scripts/build-demo-app.sh` or check `apps/*/dist/` paths |

---

## Next steps

- [USAGE.md](USAGE.md) — writing goal-based tests and configuration
- [ARCHITECTURE.md](ARCHITECTURE.md) — how the agent loop works
- [README.md](../README.md) — project overview
