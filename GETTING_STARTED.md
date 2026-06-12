# Getting Started

**MobTest** is an open-source agentic mobile test framework. You describe tests in plain English; a local LLM (Ollama) drives your app through Appium with self-healing locators.

Repository: **https://github.com/zykranai/mobTest**

---

## Who is this for?

- Mobile QA engineers who want **goal-based tests** instead of brittle locator scripts
- Teams exploring **local AI** for test automation (no API keys, runs on your machine)
- Developers who want a **working iOS + Android demo** out of the box

---

## What you need

| Platform | Required software |
|----------|-------------------|
| **All** | Java 17, Maven 3.9+, Node.js 20+, Appium 2.11.5, Ollama + `llama3.1` |
| **iOS** | macOS, Xcode, iOS Simulator |
| **Android** | Android Studio / SDK, Android Emulator |

Full install commands and verified versions: [docs/SETUP.md](docs/SETUP.md)

---

## 5-minute first run (recommended)

```bash
# 1. Clone
git clone https://github.com/zykranai/mobTest.git
cd mobTest

# 2. Install tools (Appium, Ollama, drivers, llama3.1 model)
chmod +x scripts/*.sh
./scripts/setup-environment.sh

# 3. Run everything — build, test, report, cleanup
./scripts/run-tests.sh ios
```

For a **fast smoke test** without the LLM:

```bash
./scripts/run-tests.sh ios --heuristic
```

Android (emulator must be running, or set `ANDROID_AVD`):

```bash
export ANDROID_AVD="Medium_Phone_API_36.1"   # your AVD name
./scripts/run-tests.sh android
```

Open the report:

```bash
mvn allure:serve
# or
./scripts/generate-report.sh
```

---

## What runs during the demo?

Three agentic tests against the bundled **ShopMate** e-commerce app:

1. Login flow (welcome → sign in → home)
2. Full checkout (login → product → cart → success)
3. Self-healing tap (finds button by label when id drifts)

Demo credentials: `demo@shopmate.com` / `secret123`

---

## Use your own app

1. Copy config: `cp src/test/resources/config.properties.example src/test/resources/config.local.properties`
2. Edit app path, bundle id (iOS) or package/activity (Android)
3. Copy `YourAppAgentTest.java`, write goals, add to a suite XML
4. Run: `mvn test -DsuiteXmlFile=suites/ios.xml`

Full guide: [docs/CUSTOMIZE.md](docs/CUSTOMIZE.md)

---

## Where to go next

| I want to… | Read |
|------------|------|
| Install everything step by step | [docs/SETUP.md](docs/SETUP.md) |
| Understand the stable run workflow | [docs/WORKFLOW.md](docs/WORKFLOW.md) |
| See all commands | [docs/COMMANDS.md](docs/COMMANDS.md) |
| Configure the framework | [docs/USAGE.md](docs/USAGE.md) |
| Fix a problem | [docs/FAQ.md](docs/FAQ.md) |
| Understand the architecture | [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) |
| Contribute | [CONTRIBUTING.md](CONTRIBUTING.md) |

**Full documentation index:** [docs/README.md](docs/README.md)

---

## Share this repo

You can share this link with anyone:

> **MobTest** — Agentic iOS/Android test framework (Ollama + Appium, no API keys)  
> https://github.com/zykranai/mobTest  
> Quick start: clone → `./scripts/setup-environment.sh` → `./scripts/run-tests.sh ios`

License: MIT
