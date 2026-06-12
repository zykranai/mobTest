# Documentation

Everything you need to install, run, customize, and troubleshoot **MobTest**.

**New here?** Start with [GETTING_STARTED.md](../GETTING_STARTED.md).

---

## Guides

| Document | Description |
|----------|-------------|
| [GETTING_STARTED.md](../GETTING_STARTED.md) | **Start here** — 5-minute first run |
| [SETUP.md](SETUP.md) | Full install guide with verified tool versions and copy-paste commands |
| [WORKFLOW.md](WORKFLOW.md) | Stable end-to-end workflow (build → test → report → cleanup) |
| [COMMANDS.md](COMMANDS.md) | All scripts and Maven commands in one place |
| [CUSTOMIZE.md](CUSTOMIZE.md) | Swap the demo app for your own iOS/Android app |
| [USAGE.md](USAGE.md) | Configuration, writing tests, teardown options |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Agent loop, components, self-healing |
| [FAQ.md](FAQ.md) | Common questions and troubleshooting |

---

## Quick reference

### Clone and run

```bash
git clone https://github.com/zykranai/mobTest.git
cd mobTest
./scripts/setup-environment.sh
./scripts/run-tests.sh ios
```

### Demo app credentials

- Email: `demo@shopmate.com`
- Password: `secret123`

### Default config paths

| Platform | App binary |
|----------|------------|
| iOS | `apps/shopmate-ios/dist/ShopMate.app` |
| Android | `apps/shopmate-android/dist/shopmate-debug.apk` |

### Key scripts

| Script | Purpose |
|--------|---------|
| `scripts/setup-environment.sh` | Install Appium, drivers, Ollama model |
| `scripts/run-tests.sh` | Main workflow (build, test, report, cleanup) |
| `scripts/build-demo-app.sh` | Rebuild ShopMate demo apps |
| `scripts/generate-report.sh` | Static Allure HTML report |
| `scripts/cleanup-devices.sh` | Shut down simulators/emulators |

### Test suites

| File | Platform |
|------|----------|
| `suites/default.xml` | iOS (Maven default) |
| `suites/ios.xml` | iOS |
| `suites/android.xml` | Android |

---

## Verified versions (summary)

| Tool | Version |
|------|---------|
| Java | 17+ |
| Maven | 3.9+ |
| Node.js | 20+ or 22+ |
| Appium | 2.11.5 |
| XCUITest driver | 7.28.3 |
| UiAutomator2 driver | 3.9.5 |
| Ollama | 0.30.7+ |
| Model | `llama3.1` |

Details: [SETUP.md](SETUP.md)

---

## Project layout

```
mobTest/
├── GETTING_STARTED.md     ← new users start here
├── README.md
├── apps/                  ← ShopMate demo apps + prebuilt binaries
├── docs/                  ← you are here
├── scripts/               ← setup, run, report, cleanup
├── src/main/java/         ← framework code
├── src/test/java/         ← demo + template tests
├── suites/                ← TestNG XML suites
└── pom.xml
```

---

## Community

- **License:** [MIT](../LICENSE)
- **Contributing:** [CONTRIBUTING.md](../CONTRIBUTING.md)
- **Issues:** https://github.com/zykranai/mobTest/issues
