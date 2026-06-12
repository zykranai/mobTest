# Stable Test Workflow

End-to-end flow from clone to Allure report, with cleanup at every stage.

---

## One-command run (recommended)

```bash
git clone https://github.com/zykranai/mobTest.git
cd mobTest
./scripts/setup-environment.sh

./scripts/run-tests.sh ios
# or
./scripts/run-tests.sh android
```

`run-tests.sh` does this in order:

1. **Build** demo app if `apps/*/dist/` is missing (skips if already built)
2. **Boot device** — iOS simulator or checks Android emulator (`ANDROID_AVD` can auto-start)
3. **Start services** — Ollama and Appium if not already running
4. **Preflight** — Java checks app binary exists, Appium `/status`, Ollama `/api/tags`
5. **Run tests** — `mvn clean test` with the correct suite (`suites/ios.xml` or `suites/android.xml`)
6. **Report** — generates Allure HTML in `target/site/allure-maven-plugin/`
7. **Cleanup** — terminates app + quits driver after each test; shuts down managed simulators/emulators on exit

### Flags

| Flag | Effect |
|------|--------|
| `--no-build` | Skip build step (fails if `apps/*/dist/` missing) |
| `--heuristic` | Skip Ollama, use built-in ShopMate planner |

```bash
./scripts/run-tests.sh ios --no-build --heuristic
```

---

## Manual run (your app or CI)

```bash
# 1. Config — point at your app
cp src/test/resources/config.properties.example src/test/resources/config.local.properties
# edit paths, bundle id / package

# 2. Services
ollama serve &
appium &

# 3. Device
xcrun simctl boot "iPhone 17" && open -a Simulator   # iOS
# emulator -avd Medium_Phone_API_36.1 &              # Android

# 4. Test
mvn test -DsuiteXmlFile=suites/ios.xml
mvn test -DsuiteXmlFile=suites/android.xml

# 5. Report
mvn allure:serve
# or
./scripts/generate-report.sh
```

---

## Lifecycle per test

```
@BeforeMethod
  → PreflightChecker (app file, Appium, Ollama)
  → DriverFactory.create() — new Appium session
  → TestAgent wired with tools

@Test runGoal("...")
  → agent loop (perceive → decide → act)
  → Allure step trace + screenshots on assert/failure

@AfterMethod
  → SessionManager.release() — terminate app, quit driver

Suite onFinish (SuiteLifecycleListener)
  → DeviceManager.shutdownAfterSuite() — only for managed devices
```

Set `device.managed.by.framework=true` (done automatically by `run-tests.sh`) when the script boots the device. Manual `mvn test` runs leave your open simulator alone unless you set `device.auto.boot=true`.

---

## Swap your app (3 steps)

See [CUSTOMIZE.md](CUSTOMIZE.md). Short version:

1. `config.local.properties` — your `.app` / `.apk` path and ids  
2. Copy `YourAppAgentTest.java`, write goals, add to a suite XML  
3. `mvn test -DsuiteXmlFile=suites/ios.xml` → `mvn allure:serve`

---

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| `App binary not found` | `./scripts/build-demo-app.sh ios` or fix path in config |
| `Appium is not reachable` | `appium` in another terminal |
| `Ollama is not reachable` | `ollama serve` + `ollama pull llama3.1`, or `--heuristic` |
| Android suite runs iOS | Use `-DsuiteXmlFile=suites/android.xml` (fixed in pom) |
| `config.local.properties` ignored | Fixed — local file now overlays defaults |
| Simulator left open after manual run | Expected — use `./scripts/cleanup-devices.sh ios` or `run-tests.sh` |
