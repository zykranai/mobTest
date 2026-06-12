package io.mobtest.agentic.tools;

import io.mobtest.agentic.driver.DriverFactory;
import io.mobtest.agentic.driver.Platform;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tap, type, swipe, and clear — with a locator cascade when the first strategy misses.
 */
public class ActTool implements Tool {

    @Override public String name() { return "act"; }

    @Override public String description() {
        return "Perform an action. args: action=tap|type|swipe|clear, target=<description>, "
             + "accessibilityId=<optional>, label=<optional>, text=<for type>, direction=up|down.";
    }

    @Override
    public ToolResult execute(Map<String, Object> args) {
        AppiumDriver driver = DriverFactory.get();
        String action = ArgsHelper.str(args, "action");
        try {
            switch (action) {
                case "swipe" -> {
                    swipe(driver, ArgsHelper.str(args, "direction"));
                    return ToolResult.ok("Swiped " + ArgsHelper.str(args, "direction"));
                }
                case "clear" -> {
                    LocatorHelper.Located located = locateWithScroll(driver, args);
                    if (located == null) {
                        return ToolResult.fail("Could not find target to clear: " + ArgsHelper.str(args, "target"));
                    }
                    located.element().clear();
                    return ToolResult.ok("Cleared '" + ArgsHelper.str(args, "target") + "' (via " + located.strategy() + ")");
                }
                case "tap", "type" -> {
                    LocatorHelper.Located located = locateWithScroll(driver, args);
                    if (located == null) {
                        return ToolResult.fail("Could not find target: " + ArgsHelper.str(args, "target"));
                    }
                    if (action.equals("tap")) {
                        located.element().click();
                        return ToolResult.ok("Tapped '" + ArgsHelper.str(args, "target") + "' (via " + located.strategy() + ")");
                    }
                    WebElement input = editableField(located.element(), args);
                    input.click();
                    input.clear();
                    input.sendKeys(ArgsHelper.str(args, "text"));
                    return ToolResult.ok("Typed into '" + ArgsHelper.str(args, "target") + "' (via " + located.strategy() + ")");
                }
                default -> { return ToolResult.fail("Unknown action: " + action); }
            }
        } catch (Exception e) {
            return ToolResult.fail("Action '" + action + "' failed: " + e.getMessage());
        }
    }

    private LocatorHelper.Located locateWithScroll(AppiumDriver driver, Map<String, Object> args) {
        LocatorHelper.Located located = LocatorHelper.locate(driver, args);
        if (located == null) {
            swipe(driver, "up");
            located = LocatorHelper.locate(driver, args);
        }
        return located;
    }

    private void swipe(AppiumDriver driver, String direction) {
        Dimension size = driver.manage().window().getSize();
        int midX = size.getWidth() / 2;
        int startY = "up".equals(direction) ? (int) (size.getHeight() * 0.8) : (int) (size.getHeight() * 0.2);
        int endY   = "up".equals(direction) ? (int) (size.getHeight() * 0.2) : (int) (size.getHeight() * 0.8);

        if (DriverFactory.platform() == Platform.ANDROID) {
            Map<String, Object> swipe = new HashMap<>();
            swipe.put("left", midX);
            swipe.put("top", Math.min(startY, endY));
            swipe.put("width", 1);
            swipe.put("height", Math.abs(endY - startY));
            swipe.put("direction", "up".equals(direction) ? "up" : "down");
            swipe.put("percent", 0.85);
            driver.executeScript("mobile: swipeGesture", swipe);
        } else {
            driver.executeScript("mobile: dragFromToForDuration", Map.of(
                    "duration", 0.5, "fromX", midX, "fromY", startY, "toX", midX, "toY", endY));
        }
    }

    /** Compose wraps EditText nodes — drill down on Android when needed. */
    private WebElement editableField(WebElement located, Map<String, Object> args) {
        if (DriverFactory.platform() != Platform.ANDROID) return located;
        List<WebElement> edits = located.findElements(By.xpath(".//android.widget.EditText"));
        if (!edits.isEmpty()) return edits.get(0);
        List<WebElement> all = DriverFactory.get().findElements(By.className("android.widget.EditText"));
        String accId = ArgsHelper.str(args, "accessibilityId");
        if ("login_email_field".equals(accId) && !all.isEmpty()) return all.get(0);
        if ("login_password_field".equals(accId) && all.size() >= 2) return all.get(1);
        return located;
    }
}
