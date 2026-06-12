# Usage Guide

How to run tests, configure the framework, and test your own app.

---

## Quick commands

```bash
# Environment setup (one time)
./scripts/setup-environment.sh

# Build demo apps into apps/*/dist/
./scripts/build-demo-app.sh ios
./scripts/build-demo-app.sh android

# Run tests
mvn test                                          # iOS (default suite)
mvn test -Dplatform=android -DsuiteXmlFile=suites/android.xml
mvn test -Dagent.heuristic.only=true              # fast, no LLM

# All-in-one smoke test
./scripts/run-demo-tests.sh ios
AGENT_HEURISTIC_ONLY=true ./scripts/run-demo-tests.sh android

# Reports
mvn allure:serve
```

---

## Configuration

Config is loaded in this order (highest priority first):

1. JVM system properties (`-Dplatform=android`)
2. Environment variables (`PLATFORM=android`)
3. `src/test/resources/config.local.properties` (gitignored)
4. `src/test/resources/config.properties` (committed defaults)

Copy the example for local overrides:

```bash
cp src/test/resources/config.properties.example src/test/resources/config.local.properties
```

### Key properties

| Key | Default | Description |
|-----|---------|-------------|
| `platform` | `ios` | `ios` or `android` |
| `appium.server.url` | `http://127.0.0.1:4723` | Appium server |
| `ios.app.path` | `apps/shopmate-ios/dist/ShopMate.app` | Simulator `.app` bundle |
| `ios.bundle.id` | `com.mobtest.shopmate` | iOS bundle ID |
| `ios.device.name` | `iPhone 17` | Simulator name |
| `ios.platform.version` | `26.5` | iOS runtime version |
| `android.app.path` | `apps/shopmate-android/dist/shopmate-debug.apk` | Debug APK |
| `android.app.package` | `com.mobtest.shopmate` | Launch package |
| `android.app.activity` | `com.mobtest.shopmate.MainActivity` | Launch activity |
| `ollama.endpoint` | `http://localhost:11434/api/chat` | Ollama API |
| `ollama.model` | `llama3.1` | Model name |
| `agent.heuristic.only` | `false` | Skip LLM, use built-in planner |

### Environment variable mapping

| Property | Environment variable |
|----------|---------------------|
| `platform` | `PLATFORM` |
| `ollama.model` | `OLLAMA_MODEL` |
| `agent.heuristic.only` | `AGENT_HEURISTIC_ONLY` |

---

## Writing goal-based tests

Extend `BaseTest` and call `runGoal()` with a plain-English objective:

```java
package io.mobtest.agentic.tests;

import org.testng.annotations.Test;

public class MyAppDemoTest extends BaseTest {

    @Test
    public void agentLogsIn() {
        runGoal("Sign in with user@test.com and password secret, "
                + "then verify the dashboard is visible.");
    }
}
```

Add your class to a TestNG suite under `suites/`.

### ShopMate demo tests

See `ShopMateDemoTest` for three examples:

1. Full checkout flow (login → product → cart → success)
2. Self-healing welcome tap
3. Login to home verification

---

## Testing your own app

1. Build your `.app` (iOS Simulator) or `.apk` (Android debug).
2. Create `config.local.properties`:

**iOS**

```properties
platform=ios
ios.app.path=/absolute/path/to/YourApp.app
ios.bundle.id=com.yourcompany.yourapp
ios.device.name=iPhone 17
ios.platform.version=26.5
```

**Android**

```properties
platform=android
android.app.path=/absolute/path/to/your-app-debug.apk
android.app.package=com.yourcompany.yourapp
android.app.activity=com.yourcompany.yourapp.MainActivity
android.device.name=emulator-5554
android.platform.version=16
```

3. Write goals describing user journeys in natural language.
4. Run with the appropriate suite and platform flag.

---

## Cloud testing (optional)

Set BrowserStack credentials:

```bash
export BROWSERSTACK_USERNAME=your_user
export BROWSERSTACK_ACCESS_KEY=your_key
mvn test -Drun.cloud=true
```

Ollama still runs locally for planning; BrowserStack provides the device farm.

---

## See also

- [SETUP.md](SETUP.md) — install commands and verified versions
- [ARCHITECTURE.md](ARCHITECTURE.md) — agent loop and components
