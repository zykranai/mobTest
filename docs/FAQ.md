# FAQ & Troubleshooting

---

## General

### What is MobTest?

An open-source framework for **agentic mobile testing**: you write tests as plain-English goals, a local LLM (Ollama) plans actions, and Appium executes them on real iOS/Android apps with self-healing locators.

### Do I need an API key?

No. Ollama runs fully local. Use `ollama pull llama3.1` once (~4.7 GB download).

### Can I run without the LLM?

Yes. Use heuristic mode for quick smoke tests on the bundled ShopMate demo:

```bash
./scripts/run-tests.sh ios --heuristic
# or
mvn test -Dagent.heuristic.only=true
```

Custom apps work better with Ollama — the heuristic planner is tuned for ShopMate.

### Does it work on Windows?

iOS testing requires **macOS + Xcode**. Android tests can run on Windows/Linux with Android SDK + emulator, but the project is primarily verified on **macOS (Apple Silicon)**.

---

## Installation

### `mvn: command not found`

Install Maven 3.9+:

```bash
brew install maven          # macOS
sudo apt install maven      # Ubuntu
```

### `appium: command not found`

```bash
npm install -g appium@2.11.5
appium driver install xcuitest@7.28.3
appium driver install uiautomator2@3.9.5
```

Or run `./scripts/setup-environment.sh`.

### Ollama model not found

```bash
ollama pull llama3.1
ollama serve
```

---

## Running tests

### `App binary not found`

Build the demo app or fix your config path:

```bash
./scripts/build-demo-app.sh ios
# or
./scripts/build-demo-app.sh android
```

For your own app, set `ios.app.path` / `android.app.path` in `config.local.properties`.

### `Appium is not reachable`

Start Appium in a separate terminal:

```bash
appium
curl http://127.0.0.1:4723/status
```

### `Ollama is not reachable`

```bash
ollama serve
curl http://localhost:11434/api/tags
```

Or use `--heuristic` to skip Ollama.

### Android suite runs iOS tests

Use the Android suite explicitly:

```bash
mvn test -DsuiteXmlFile=suites/android.xml
# or
./scripts/run-tests.sh android
```

### `Could not find simulator` (iOS)

List available simulators and update config:

```bash
xcrun simctl list devices available
```

Edit `ios.device.name` and `ios.platform.version` in `config.properties` or `config.local.properties`.

### No Android emulator running

Start one manually:

```bash
emulator -list-avds
emulator -avd Your_Avd_Name &
```

Or let the script start it:

```bash
export ANDROID_AVD="Medium_Phone_API_36.1"
./scripts/run-tests.sh android
```

### Agent fails / max steps reached

- Ensure Ollama is running and `llama3.1` is pulled
- Try a simpler, shorter goal in one test
- Check Appium logs and the Allure agent step trace
- For ShopMate demo, try `--heuristic` to verify the pipeline works

### `config.local.properties` not applied

Local config **overlays** defaults (fixed in recent versions). Ensure the file is at:

`src/test/resources/config.local.properties`

It is gitignored — copy from `config.properties.example`.

---

## Reports

### No Allure results

Run tests first. Results go to `target/allure-results/`.

```bash
mvn allure:serve
./scripts/generate-report.sh
```

### Where are screenshots?

Attached in Allure on assertions and failed agent goals. Open the report and expand test steps → attachments.

---

## Devices & cleanup

### Simulator still running after manual `mvn test`

Expected when you boot the simulator yourself. Cleanup only shuts down **managed** devices (started by `run-tests.sh` or `device.auto.boot=true`).

Manual cleanup:

```bash
./scripts/cleanup-devices.sh ios
```

### Physical Android device connected

The framework will not kill physical devices — only emulators (`emulator-*` serials).

---

## Custom apps

### How do I test my own app?

See [CUSTOMIZE.md](CUSTOMIZE.md). Summary:

1. `config.local.properties` — paths and bundle/package ids
2. Copy `YourAppAgentTest.java`, write `runGoal("...")`
3. Add class to `suites/ios.xml` or `suites/android.xml`
4. `mvn test -DsuiteXmlFile=suites/ios.xml`

### Do I need locators in test code?

No. Describe goals in English. The agent perceives the screen and self-heals via multiple locator strategies.

---

## Sharing & contributing

### Can I share this repo?

Yes. MIT license. Share: **https://github.com/zykranai/mobTest**

### How do I contribute?

See [CONTRIBUTING.md](../CONTRIBUTING.md). Fork → branch → run tests → pull request.

---

## Still stuck?

1. Check [SETUP.md](SETUP.md) for install steps
2. Check [WORKFLOW.md](WORKFLOW.md) for the full run sequence
3. Open an issue: https://github.com/zykranai/mobTest/issues

Include: OS version, platform (iOS/Android), command you ran, error message, and Appium/Ollama status.
