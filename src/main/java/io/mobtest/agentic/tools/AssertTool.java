package io.mobtest.agentic.tools;

import io.mobtest.agentic.driver.DriverFactory;
import io.mobtest.agentic.driver.Platform;
import io.mobtest.agentic.reporting.ScreenshotHelper;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

/**
 * Checks that expected text is on screen. Attaches a screenshot either way.
 */
public class AssertTool implements Tool {

    @Override public String name() { return "assert_visible"; }

    @Override public String description() {
        return "Assert expected text is visible. args: expected=<text>.";
    }

    @Override
    public ToolResult execute(Map<String, Object> args) {
        AppiumDriver driver = DriverFactory.get();
        String expected = args.getOrDefault("expected", "").toString();
        try {
            By by = DriverFactory.platform() == Platform.ANDROID
                    ? By.xpath("//*[contains(@text," + LocatorHelper.xp(expected) + ") "
                             + "or contains(@content-desc," + LocatorHelper.xp(expected) + ")]")
                    : By.xpath("//*[contains(@label," + LocatorHelper.xp(expected) + ") "
                             + "or contains(@name," + LocatorHelper.xp(expected) + ") "
                             + "or contains(@value," + LocatorHelper.xp(expected) + ")]");

            List<WebElement> matches = driver.findElements(by);
            boolean found = matches.stream().anyMatch(WebElement::isDisplayed);
            ScreenshotHelper.attach(found ? "Assertion pass" : "Assertion fail");
            String tag = found ? "[PASS]" : "[FAIL]";
            String obs = tag + " expected '" + expected + "' " + (found ? "is" : "is NOT") + " visible.";
            return found ? ToolResult.ok(obs) : ToolResult.fail(obs);
        } catch (Exception e) {
            ScreenshotHelper.attach("Assertion error");
            return ToolResult.fail("Assertion error: " + e.getMessage());
        }
    }
}
