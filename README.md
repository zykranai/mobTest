# MobTest — Agentic Mobile Test Framework

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java 17](https://img.shields.io/badge/Java-17+-orange.svg)](docs/SETUP.md)
[![Appium 2](https://img.shields.io/badge/Appium-2.11.5-green.svg)](docs/SETUP.md)
[![Ollama](https://img.shields.io/badge/Ollama-llama3.1-purple.svg)](docs/SETUP.md)

**Clone → install → run agentic tests on bundled demo apps → swap in your own iOS or Android app.**

An open-source framework where a **local Ollama LLM** (`llama3.1`) drives **live mobile apps** through **Appium**: perceive the screen, decide the next action, act with **self-healing locators**, and verify goals in plain English. **No API keys. No cloud LLM required.**

🔗 **https://github.com/zykranai/mobTest**

---

## New here?

| Step | Link |
|------|------|
| **1. First run in 5 minutes** | [GETTING_STARTED.md](GETTING_STARTED.md) |
| **2. Full install guide** | [docs/SETUP.md](docs/SETUP.md) |
| **3. Use your own app** | [docs/CUSTOMIZE.md](docs/CUSTOMIZE.md) |
| **4. All documentation** | [docs/README.md](docs/README.md) |
| **5. Problems?** | [docs/FAQ.md](docs/FAQ.md) |

---

## Quick start

```bash
git clone https://github.com/zykranai/mobTest.git
cd mobTest
chmod +x scripts/*.sh
./scripts/setup-environment.sh
./scripts/run-tests.sh ios
```

Android:

```bash
export ANDROID_AVD="Your_Avd_Name"    # optional — auto-starts emulator
./scripts/run-tests.sh android
```

Fast smoke test (no LLM):

```bash
./scripts/run-tests.sh ios --heuristic
```

Open report: `mvn allure:serve` or `./scripts/generate-report.sh`

---

## What you get

| Piece | Location | Description |
|-------|----------|-------------|
| **Framework** | `src/main/java/io/mobtest/agentic/` | Agent loop, Ollama client, tools, driver factory |
| **iOS demo app** | `apps/shopmate-ios/` | ShopMate — SwiftUI e-commerce demo |
| **Android demo app** | `apps/shopmate-android/` | ShopMate — Jetpack Compose demo |
| **Prebuilt binaries** | `apps/*/dist/` | Ready-to-run `.app` and `.apk` for first test |
| **Demo tests** | `src/test/java/io/mobtest/agentic/tests/` | Goal-based TestNG tests (no locators) |
| **Your app template** | `YourAppAgentTest.java` | Copy, edit, enable — see [CUSTOMIZE.md](docs/CUSTOMIZE.md) |

**Demo flow:** Welcome → Login → Home → Products → Cart → Checkout → Success

**Demo credentials:** `demo@shopmate.com` / `secret123`

---

## How it works

```
Plain-English goal  →  TestAgent (perceive → Ollama decide → act)
                   →  Appium (iOS XCUITest / Android UiAutomator2)
                   →  Allure report (step trace + screenshots)
```

After each test: app terminated, driver quit. After suite: simulators/emulators shut down (when started by `run-tests.sh`).

Details: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) · [docs/WORKFLOW.md](docs/WORKFLOW.md)

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

No locators in test code — the agent reads the live screen each step.

---

## Documentation

| Guide | Contents |
|-------|----------|
| [GETTING_STARTED.md](GETTING_STARTED.md) | **Start here** — first run, what you need |
| [docs/README.md](docs/README.md) | Full documentation index |
| [docs/SETUP.md](docs/SETUP.md) | Install commands + verified versions |
| [docs/WORKFLOW.md](docs/WORKFLOW.md) | Stable workflow (build → test → report → cleanup) |
| [docs/COMMANDS.md](docs/COMMANDS.md) | All scripts and Maven commands |
| [docs/CUSTOMIZE.md](docs/CUSTOMIZE.md) | Test your own app |
| [docs/USAGE.md](docs/USAGE.md) | Configuration reference |
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | Agent loop and components |
| [docs/FAQ.md](docs/FAQ.md) | FAQ and troubleshooting |
| [CONTRIBUTING.md](CONTRIBUTING.md) | How to contribute |

---

## Prerequisites

| Tool | iOS | Android |
|------|-----|---------|
| Java 17+ | ✓ | ✓ |
| Maven 3.9+ | ✓ | ✓ |
| Node.js 20+ | ✓ | ✓ |
| Appium 2.11.5 + drivers | xcuitest 7.28.3 | uiautomator2 3.9.5 |
| Ollama + llama3.1 | ✓ (or `--heuristic`) | ✓ |
| Xcode + Simulator | ✓ | — |
| Android SDK + Emulator | — | ✓ |

Install everything: `./scripts/setup-environment.sh` — details in [docs/SETUP.md](docs/SETUP.md).

---

## Project structure

```
mobTest/
├── GETTING_STARTED.md         # new users start here
├── README.md
├── apps/                      # ShopMate demo apps + prebuilt dist/
├── docs/                      # full documentation
├── scripts/
│   ├── setup-environment.sh # install Appium, Ollama, drivers
│   ├── run-tests.sh           # main workflow entry point
│   ├── build-demo-app.sh
│   ├── generate-report.sh
│   └── cleanup-devices.sh
├── src/main/java/io/mobtest/agentic/   # framework
├── src/test/java/io/mobtest/agentic/   # tests + YourAppAgentTest template
├── suites/                    # TestNG: default.xml, ios.xml, android.xml
└── pom.xml
```

---

## Share this project

You can share the repo publicly under the MIT license:

> **MobTest** — Agentic iOS/Android test automation with Ollama + Appium (no API keys)  
> https://github.com/zykranai/mobTest  
> Quick start: `git clone …` → `./scripts/setup-environment.sh` → `./scripts/run-tests.sh ios`

---

## License

MIT — see [LICENSE](LICENSE). Copyright (c) 2026 zykranai.
