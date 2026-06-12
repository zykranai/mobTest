# MobTest — Agentic Mobile Test Framework

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Clone → install → run agentic tests on bundled demo apps → swap in your own iOS or Android app.**

An open-source framework where a **local Ollama LLM** (`llama3.1`) drives **live mobile apps** through **Appium**: perceive the screen, decide the next action, act with **self-healing locators**, and verify goals in plain English.

Repository: [github.com/zykranai/mobTest](https://github.com/zykranai/mobTest)

---

## What you get

| Piece | Location | Description |
|-------|----------|-------------|
| **Framework** | `src/main/java/io/mobtest/agentic/` | Agent loop, Ollama client, tools, driver factory |
| **iOS demo app** | `apps/shopmate-ios/` | ShopMate — SwiftUI e-commerce demo |
| **Android demo app** | `apps/shopmate-android/` | ShopMate — Jetpack Compose demo |
| **Prebuilt binaries** | `apps/*/dist/` | Ready-to-run `.app` and `.apk` for first test |
| **Demo tests** | `src/test/java/io/mobtest/agentic/tests/` | Goal-based TestNG tests (no locators) |

Demo flow: **Welcome → Login → Home → Products → Cart → Checkout → Success**

Credentials: `demo@shopmate.com` / `secret123`

---

## Quick start

```bash
git clone https://github.com/zykranai/mobTest.git
cd mobTest

# 1. Install Appium, Ollama, pull llama3.1
chmod +x scripts/*.sh
./scripts/setup-environment.sh

# 2. Start services (separate terminals)
ollama serve
appium

# 3. Boot a device
xcrun simctl boot "iPhone 17" && open -a Simulator   # iOS
# emulator -avd Medium_Phone_API_36.1 &              # Android

# 4. Run tests (prebuilt demo apps included)
mvn test                                              # iOS
mvn test -Dplatform=android -DsuiteXmlFile=suites/android.xml
```

**One-command workflow (build → test → report → cleanup):**

```bash
./scripts/run-tests.sh ios
./scripts/run-tests.sh android
./scripts/run-tests.sh ios --no-build --heuristic   # fast smoke
```

Fast validation without LLM:

```bash
mvn test -Dagent.heuristic.only=true
```

After tests finish, the framework **terminates the app, quits the Appium driver, and shuts down simulators/emulators** so nothing is left running. Reports land in `target/allure-results` — open with `mvn allure:serve` or `./scripts/generate-report.sh`.

**Your app:** copy `config.properties.example` → `config.local.properties`, edit paths, start from `YourAppAgentTest.java`. Details in [docs/CUSTOMIZE.md](docs/CUSTOMIZE.md).

---

## Documentation

| Guide | Contents |
|-------|----------|
| [**docs/WORKFLOW.md**](docs/WORKFLOW.md) | **Stable run workflow** — one command from clone to report |
| [**docs/SETUP.md**](docs/SETUP.md) | Full install commands, verified versions, Xcode/Android/Ollama setup |
| [**docs/CUSTOMIZE.md**](docs/CUSTOMIZE.md) | **Swap your app** — config, test template, reports |
| [**docs/USAGE.md**](docs/USAGE.md) | Configuration, writing tests, teardown options |
| [**docs/ARCHITECTURE.md**](docs/ARCHITECTURE.md) | Agent loop, self-healing, component map |

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

---

## Sample test

```java
@Test
public void agentCompletesCheckoutFlow() {
    runGoal("In ShopMate, sign in with demo@shopmate.com / secret123, open "
            + "Electronics, add Wireless Headphones to cart, open the cart, "
            + "tap Checkout, and verify 'Order Placed Successfully!' is shown.");
}
```

---

## Project structure

```
mobTest/
├── apps/
│   ├── shopmate-ios/          # SwiftUI demo + dist/ShopMate.app
│   └── shopmate-android/      # Compose demo + dist/shopmate-debug.apk
├── docs/
│   ├── SETUP.md               # Install guide (start here)
│   ├── USAGE.md
│   └── ARCHITECTURE.md
├── scripts/
│   ├── setup-environment.sh
│   ├── build-demo-app.sh
│   ├── run-tests.sh              # main workflow entry point
│   ├── generate-report.sh
│   └── cleanup-devices.sh
├── src/main/java/io/mobtest/agentic/
├── src/test/java/io/mobtest/agentic/tests/
├── suites/                    # TestNG suites (ios, android, default)
└── pom.xml
```

---

## Prerequisites

| Tool | iOS | Android |
|------|-----|---------|
| Java 17+ | ✓ | ✓ |
| Maven 3.9+ | ✓ | ✓ |
| Node.js 20+ | ✓ | ✓ |
| Appium 2.11.5 | + xcuitest 7.28.3 | + uiautomator2 3.9.5 |
| Ollama + llama3.1 | ✓ | ✓ |
| Xcode + Simulator | ✓ | — |
| Android SDK + Emulator | — | ✓ |

See [docs/SETUP.md](docs/SETUP.md) for exact install commands.

---

## License

MIT — see [LICENSE](LICENSE).
