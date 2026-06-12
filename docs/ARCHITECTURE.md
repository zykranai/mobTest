# Architecture

The MobTest framework uses an **agent loop** to drive real mobile apps through Appium. Tests are written as plain-English goals — no locators in test code.

---

## High-level flow

```
Plain-English goal (TestNG test)
        │
        ▼
   TestAgent loop
   ┌─────────────────────────────────────┐
   │ 1. Perceive  → screen snapshot      │
   │ 2. Decide    → Ollama or heuristic  │
   │ 3. Act       → tap / type / scroll  │
   │ 4. Observe   → check result         │
   │ 5. Assert    → goal met? repeat     │
   └─────────────────────────────────────┘
        │
        ▼
   Appium (XCUITest / UiAutomator2)
        │
        ▼
   ShopMate demo app (or your app)
        │
        ▼
   Allure report (steps + screenshots)
```

---

## Core components

| Package | Class | Role |
|---------|-------|------|
| `io.mobtest.agentic.agent` | `TestAgent` | Main perceive → decide → act loop |
| | `LlmClient` | Calls Ollama chat API with tool schema |
| | `HeuristicPlanner` | Rule-based fallback when LLM is off |
| `io.mobtest.agentic.tools` | `PerceiveTool` | Reads visible elements from the live UI tree |
| | `ActTool` | Executes tap, type, scroll with self-healing |
| | `AssertTool` | Verifies text/element presence |
| | `LocatorHelper` | Locator cascade (accessibility id → label → xpath) |
| `io.mobtest.agentic.config` | `ConfigLoader` | Loads `config.properties` + local overrides |
| `io.mobtest.agentic.driver` | `DriverFactory` | Creates iOS or Android Appium session |
| | `SessionManager` | Terminate app + quit driver after each test |
| | `DeviceManager` | Boot/shutdown simulators and emulators |
| `io.mobtest.agentic.support` | `PreflightChecker` | Validates app path, Appium, Ollama before session |
| | `SuiteLifecycleListener` | Shuts down managed devices after suite |
| `io.mobtest.agentic.reporting` | `AgentReporter` | Allure step traces + failure screenshots |

---

## Self-healing locators

When the agent taps or types, `ActTool` tries multiple strategies in order:

1. Accessibility ID / content-desc
2. Visible label or text
3. Platform-specific XPath (including Compose `EditText` on Android)
4. Scroll-and-retry

The strategy that succeeds is logged in the Allure report for debugging.

---

## LLM vs heuristic mode

| Mode | Flag | When to use |
|------|------|-------------|
| **Ollama (default)** | `agent.heuristic.only=false` | Full agentic planning with `llama3.1` |
| **Heuristic** | `-Dagent.heuristic.only=true` | CI smoke tests, no GPU/LLM needed |

The heuristic planner encodes known flows for the ShopMate demo (welcome → login → browse → checkout). Custom apps benefit more from Ollama mode.

---

## Demo applications

Bundled under `apps/`:

| App | Path | Stack |
|-----|------|-------|
| **shopmate-ios** | `apps/shopmate-ios/` | SwiftUI, bundle `com.mobtest.shopmate` |
| **shopmate-android** | `apps/shopmate-android/` | Jetpack Compose, package `com.mobtest.shopmate` |

Prebuilt binaries for first-run tests:

- `apps/shopmate-ios/dist/ShopMate.app`
- `apps/shopmate-android/dist/shopmate-debug.apk`

Flow: **Welcome → Login → Home → Products → Cart → Checkout → Success**

Demo credentials: `demo@shopmate.com` / `secret123`

---

## Test suites

| File | Platform |
|------|----------|
| `suites/default.xml` | iOS (Maven default) |
| `suites/ios.xml` | iOS explicit |
| `suites/android.xml` | Android |

Demo tests live in `io.mobtest.agentic.tests.ShopMateDemoTest`.
