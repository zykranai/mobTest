package io.mobtest.agentic.tests;

import io.qameta.allure.Description;
import org.testng.annotations.Test;

/**
 * Starter template for your own app.
 *
 * 1. Copy config.properties.example → config.local.properties
 * 2. Set ios.app.path / android.app.path and bundle id or package name
 * 3. Rename this class, enable the tests below, and describe your flows in English
 * 4. Add the class to suites/ios.xml or suites/android.xml
 *
 * Tests are disabled by default so the demo suite keeps passing out of the box.
 */
public class YourAppAgentTest extends BaseTest {

    @Test(enabled = false, description = "Replace with your first user journey")
    @Description("Example: sign in and land on the home screen.")
    public void yourFirstFlow() {
        runGoal("Open the app, sign in with user@example.com and password secret123, "
                + "then verify the home screen is visible.");
    }

    @Test(enabled = false, description = "Another flow — enable when ready")
    @Description("Example: navigate to settings and check a label.")
    public void yourSecondFlow() {
        runGoal("From the home screen, open Settings and verify 'Notifications' is shown.");
    }
}
