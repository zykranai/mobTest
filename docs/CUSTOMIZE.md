# Use the Framework With Your Own App

Three steps: point config at your build, write goals in English, run tests and open the report.

---

## 1. Point config at your app

```bash
cp src/test/resources/config.properties.example src/test/resources/config.local.properties
```

Edit `config.local.properties`:

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
android.app.path=/absolute/path/to/app-debug.apk
android.app.package=com.yourcompany.yourapp
android.app.activity=com.yourcompany.yourapp.MainActivity
android.device.name=emulator-5554
android.platform.version=16
```

Paths can be relative to the project root (e.g. `apps/my-app/dist/my.apk`).

---

## 2. Add a test class

Copy `YourAppAgentTest.java`, rename the class, enable the tests, and describe flows in plain English:

```java
public class LoginFlowTest extends BaseTest {

    @Test
    public void userCanSignIn() {
        runGoal("Sign in with test@example.com and password secret123, "
                + "then verify the dashboard is visible.");
    }
}
```

Register the class in `suites/ios.xml` or `suites/android.xml`:

```xml
<class name="io.mobtest.agentic.tests.LoginFlowTest"/>
```

No locators in test code — the agent reads the screen and self-heals when labels drift.

---

## 3. Run and view the report

```bash
ollama serve &    # terminal 1
appium &          # terminal 2

# Boot your device, then:
mvn test -DsuiteXmlFile=suites/ios.xml
# or
mvn test -Dplatform=android -DsuiteXmlFile=suites/android.xml

# HTML report
mvn allure:serve
# or static report
./scripts/generate-report.sh
```

After the suite finishes, simulators/emulators are shut down automatically (`device.shutdown.after.suite=true`). Each test also terminates the app and quits the Appium driver so the next test starts clean.

---

## Teardown settings

| Property | Default | What it does |
|----------|---------|--------------|
| `driver.cleanup.after.test` | `true` | Terminate app + quit driver after each `@Test` |
| `device.shutdown.after.suite` | `true` | Shut down simulator/emulator when suite ends |
| `device.auto.boot` | `false` | Boot iOS simulator from Java before each test |

Set any of these to `false` in `config.local.properties` if you prefer to manage devices yourself.

Manual cleanup:

```bash
./scripts/cleanup-devices.sh ios
./scripts/cleanup-devices.sh android emulator-5554
```

---

## Tips for reliable goals

- Name screens and buttons the way a user would ("tap Sign In", not a locator id).
- One clear outcome per test ("verify … is visible").
- Use `-Dagent.heuristic.only=true` only for quick smoke runs — custom apps work better with Ollama (`llama3.1`).

See also [USAGE.md](USAGE.md) and [SETUP.md](SETUP.md).
