# Agentic AI Mobile Test Framework

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Clone → install → run agentic tests on bundled sample apps → swap in your own iOS or Android app.**

An open-source framework where a **local Ollama LLM** drives **live mobile apps** through **Appium**: perceive the screen, decide the next action, act with **self-healing locators**, and verify goals in plain English.

Repository: [github.com/zykranai/mobTest](https://github.com/zykranai/mobTest)

---

## What you get

| Piece | Location | Description |
|-------|----------|-------------|
| **Agent framework** | `src/main/java/com/demo/` | Agent loop, Ollama client, tools, driver factory |
| **iOS sample app** | `sample-apps/ios/` | ShopMate — SwiftUI e-commerce demo |
| **Android sample app** | `sample-apps/android/` | ShopMate — Jetpack Compose demo (same flows) |
| **Agentic tests** | `src/test/java/com/demo/tests/` | Goal-based TestNG tests (no locators in test code) |

Both sample apps share the same user journey:

**Welcome → Login → Home → Products → Cart → Checkout → Success**

Demo credentials: `demo@shopmate.com` / `secret123`

---

## Architecture

```
Plain-English goal
       ↓
TestAgent  (perceive → Ollama decide → act → observe)
       ↓
PerceiveTool | ActTool (self-healing) | AssertTool
       ↓
Appium  →  iOS (XCUITest) or Android (UiAutomator2)
       ↓
Allure step trace + screenshots
```

**Self-healing** tries multiple locator strategies (accessibility id → label/text → content-desc → scroll retry) and logs which one worked.

**Hybrid brain**: [Ollama](https://ollama.com) (`llama3.1`) by default; built-in `HeuristicPlanner` fallback when Ollama is offline (`-Dagent.heuristic.only=true`).

---

## Prerequisites

| Tool | iOS | Android |
|------|-----|---------|
| **Java 17+** | ✓ | ✓ |
| **Maven 3.9+** | ✓ | ✓ |
| **Node.js 20+** | ✓ (Appium) | ✓ (Appium) |
| **Appium 2** | + xcuitest driver | + uiautomator2 driver |
| **Ollama** | ✓ (local LLM) | ✓ |
| **Xcode + Simulator** | ✓ | — |
| **Android SDK + Emulator** | — | ✓ |

---

## Quick start (5 steps)

```bash
git clone https://github.com/zykranai/mobTest.git
cd mobTest

# 1. Install Appium, Ollama, pull model
./scripts/setup-tools.sh

# 2. Build the sample app
./scripts/build-sample-app.sh ios        # or: android

# 3. Start services (separate terminals)
ollama serve
appium

# 4. Boot a device
# iOS:
xcrun simctl boot "iPhone 17" && open -a Simulator
# Android:
emulator -avd Pixel_7_API_34 &

# 5. Run agentic tests
mvn test                                          # iOS (default)
mvn test -Dplatform=android -DsuiteXmlFile=testng-android.xml
```

**One-liner smoke test** (starts Ollama + Appium if needed):

```bash
./scripts/run-tests.sh ios
./scripts/run-tests.sh android
```

Fast validation without LLM:

```bash
mvn test -Dagent.heuristic.only=true
```

Allure report: `mvn allure:serve`

---

## Using your own app

1. Copy the example config:
   ```bash
   cp src/test/resources/config.properties.example src/test/resources/config.local.properties
   ```

2. Edit `config.local.properties`:

   **iOS**
   ```properties
   platform=ios
   ios.app.path=/path/to/YourApp.app
   ios.bundle.id=com.yourcompany.yourapp
   ios.device.name=iPhone 17
   ios.platform.version=17.0
   ```

   **Android**
   ```properties
   platform=android
   android.app.path=/path/to/your-app-debug.apk
   android.app.package=com.yourcompany.yourapp
   android.app.activity=com.yourcompany.yourapp.MainActivity
   android.device.name=emulator-5554
   ```

3. Write a goal-based test (no locators):
   ```java
   @Test
   public void agentLogsIn() {
       runGoal("Sign in with user@test.com and password secret, then verify the dashboard is visible.");
   }
   ```

4. Run: `mvn test -Dplatform=ios` or `-Dplatform=android`

> **Ollama needs no API key** — it runs fully local. To use a different model, change `ollama.model` in config (e.g. `mistral`, `llama3.2`).

---

## Configuration reference

| Key | Description |
|-----|-------------|
| `platform` | `ios` or `android` |
| `ollama.endpoint` | Default `http://localhost:11434/api/chat` |
| `ollama.model` | Default `llama3.1` |
| `agent.heuristic.only` | `true` = skip LLM, use built-in planner |
| `ios.app.path` / `android.app.path` | Path to `.app` or `.apk` |
| `ios.bundle.id` | iOS bundle identifier |
| `android.app.package` / `android.app.activity` | Android launch intent |

Environment variables override properties (e.g. `PLATFORM=android`, `OLLAMA_MODEL=llama3.1`).

---

## Project structure

```
mobTest/
├── sample-apps/
│   ├── ios/ShopMate.xcodeproj      # SwiftUI sample
│   └── android/                    # Compose sample (Gradle)
├── src/main/java/com/demo/
│   ├── agent/                      # TestAgent, LlmClient, HeuristicPlanner
│   ├── tools/                      # Perceive, Act, Assert, LocatorHelper
│   ├── driver/                     # DriverFactory (iOS + Android)
│   └── reporting/                  # Allure step trace
├── src/test/
│   ├── java/.../ShopMateAgentTest.java
│   └── resources/config.properties.example
├── scripts/
│   ├── setup-tools.sh
│   ├── build-sample-app.sh ios|android
│   └── run-tests.sh ios|android
├── testng-ios.xml
├── testng-android.xml
└── pom.xml
```

---

## Sample agentic test

```java
@Test
public void agentCompletesCheckoutFlow() {
    runGoal("In ShopMate, sign in with demo@shopmate.com / secret123, open "
            + "Electronics, add Wireless Headphones to cart, open the cart, "
            + "tap Checkout, and verify 'Order Placed Successfully!' is shown.");
}
```

The agent perceives the live screen each step and self-heals when locators drift.

---

## Cloud testing (optional)

Set BrowserStack credentials and run:

```bash
export BROWSERSTACK_USERNAME=...
export BROWSERSTACK_ACCESS_KEY=...
mvn test -Drun.cloud=true
```

Ollama still runs locally; the cloud provides the device.

---

## License

MIT — see [LICENSE](LICENSE).
