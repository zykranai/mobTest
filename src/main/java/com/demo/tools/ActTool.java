package com.demo.tools;

import com.demo.driver.DriverFactory;
import io.appium.java_client.AppiumDriver;

import java.util.Map;

/**
 * ACT — tap, type, swipe with a self-healing locator cascade (iOS + Android).
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
        String action = str(args, "action");
        try {
            switch (action) {
                case "swipe" -> {
                    swipe(driver, str(args, "direction"));
                    return ToolResult.ok("Swiped " + str(args, "direction"));
                }
                case "clear" -> {
                    LocatorHelper.Located located = locateWithScroll(driver, args);
                    if (located == null) {
                        return ToolResult.fail("Could not find target to clear: " + str(args, "target"));
                    }
                    located.element().clear();
                    return ToolResult.ok("Cleared '" + str(args, "target") + "' (via " + located.strategy() + ")");
                }
                case "tap", "type" -> {
                    LocatorHelper.Located located = locateWithScroll(driver, args);
                    if (located == null) {
                        return ToolResult.fail("Could not find target: " + str(args, "target"));
                    }
                    if (action.equals("tap")) {
                        located.element().click();
                        return ToolResult.ok("Tapped '" + str(args, "target") + "' (via " + located.strategy() + ")");
                    }
                    located.element().click();
                    located.element().clear();
                    located.element().sendKeys(str(args, "text"));
                    return ToolResult.ok("Typed into '" + str(args, "target") + "' (via " + located.strategy() + ")");
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
        var size = driver.manage().window().getSize();
        int midX = size.getWidth() / 2;
        int startY = "up".equals(direction) ? (int) (size.getHeight() * 0.8) : (int) (size.getHeight() * 0.2);
        int endY   = "up".equals(direction) ? (int) (size.getHeight() * 0.2) : (int) (size.getHeight() * 0.8);
        driver.executeScript("mobile: dragFromToForDuration", Map.of(
                "duration", 0.5, "fromX", midX, "fromY", startY, "toX", midX, "toY", endY));
    }

    private static String str(Map<String, Object> a, String k) {
        Object v = a.get(k);
        return v == null ? "" : v.toString();
    }
}
