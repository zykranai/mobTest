# Command Reference

All commands for installing, running, reporting, and cleaning up MobTest.

---

## Setup (one time)

```bash
chmod +x scripts/*.sh
./scripts/setup-environment.sh
```

Installs/checks: Java 17, Maven, Node.js, Appium 2.11.5, xcuitest + uiautomator2 drivers, Ollama `llama3.1`.

Manual installs: see [SETUP.md](SETUP.md).

---

## Main workflow

```bash
./scripts/run-tests.sh ios
./scripts/run-tests.sh android
./scripts/run-tests.sh ios --no-build          # skip build if dist/ exists
./scripts/run-tests.sh ios --heuristic         # no Ollama, built-in planner
./scripts/run-tests.sh android --no-build --heuristic
```

Environment variables:

```bash
IOS_DEVICE="iPhone 17" ./scripts/run-tests.sh ios
ANDROID_DEVICE="emulator-5554" ./scripts/run-tests.sh android
ANDROID_AVD="Medium_Phone_API_36.1" ./scripts/run-tests.sh android   # auto-start emulator
AGENT_HEURISTIC_ONLY=true ./scripts/run-tests.sh ios                 # same as --heuristic
```

---

## Build demo apps

```bash
./scripts/build-demo-app.sh ios
./scripts/build-demo-app.sh android
```

Output:

- iOS: `apps/shopmate-ios/dist/ShopMate.app`
- Android: `apps/shopmate-android/dist/shopmate-debug.apk`

---

## Run tests manually (Maven)

```bash
# iOS (default suite)
mvn test

# iOS explicit suite
mvn test -DsuiteXmlFile=suites/ios.xml

# Android
mvn test -DsuiteXmlFile=suites/android.xml

# Fast mode (no LLM)
mvn test -Dagent.heuristic.only=true
mvn test -DsuiteXmlFile=suites/android.xml -Dagent.heuristic.only=true

# Cloud (BrowserStack — optional)
export BROWSERSTACK_USERNAME=...
export BROWSERSTACK_ACCESS_KEY=...
mvn test -Drun.cloud=true
```

---

## Services (if not using run-tests.sh)

```bash
ollama serve                    # terminal 1
ollama pull llama3.1            # one-time

appium                          # terminal 2

# iOS device
xcrun simctl boot "iPhone 17"
open -a Simulator

# Android device
emulator -avd Medium_Phone_API_36.1 &
adb devices
```

---

## Reports

```bash
mvn allure:serve                              # interactive server
./scripts/generate-report.sh                  # static HTML
open target/site/allure-maven-plugin/index.html
```

Results directory: `target/allure-results/`

---

## Cleanup

```bash
./scripts/cleanup-devices.sh ios
./scripts/cleanup-devices.sh android
./scripts/cleanup-devices.sh android emulator-5554
```

Automatic cleanup also runs at the end of `./scripts/run-tests.sh` and after each test suite (driver quit + managed device shutdown).

---

## Compile only (no device needed)

```bash
mvn clean test-compile
```

---

## Useful diagnostics

```bash
java -version
mvn -version
node -v
appium -v
appium driver list --installed
ollama -v
ollama list

curl http://127.0.0.1:4723/status          # Appium
curl http://localhost:11434/api/tags         # Ollama

xcrun simctl list devices available          # iOS simulators
emulator -list-avds                          # Android AVDs
adb devices
```
