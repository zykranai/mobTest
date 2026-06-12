package com.demo.tools;

import com.demo.driver.DriverFactory;
import com.demo.driver.Platform;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

/**
 * PERCEIVE — compact text view of interactable on-screen elements (iOS + Android).
 */
public class PerceiveTool implements Tool {

    @Override public String name() { return "read_screen"; }

    @Override public String description() {
        return "Read the current screen. Returns visible interactable elements.";
    }

    @Override
    public ToolResult execute(Map<String, Object> args) {
        AppiumDriver driver = DriverFactory.get();
        try {
            String xpath = DriverFactory.platform() == Platform.ANDROID
                    ? "//*[@clickable='true' or @focusable='true' or @class='android.widget.EditText']"
                    : "//*[self::XCUIElementTypeButton or self::XCUIElementTypeStaticText "
                      + "or self::XCUIElementTypeTextField or self::XCUIElementTypeSecureTextField "
                      + "or self::XCUIElementTypeSwitch or self::XCUIElementTypeCell "
                      + "or self::XCUIElementTypeOther]";

            StringBuilder sb = new StringBuilder("Visible elements:\n");
            int i = 0;
            for (WebElement el : driver.findElements(org.openqa.selenium.By.xpath(xpath))) {
                if (!el.isDisplayed()) continue;
                if (DriverFactory.platform() == Platform.ANDROID) {
                    String text = safe(el.getAttribute("text"));
                    String desc = safe(el.getAttribute("content-desc"));
                    String id   = safe(el.getAttribute("resource-id"));
                    String cls  = safe(el.getAttribute("class"));
                    if (text.isBlank() && desc.isBlank() && id.isBlank()) continue;
                    sb.append(String.format("  [%d] %s | text='%s' desc='%s' id='%s'%n",
                            i++, shortClass(cls), text, desc, id));
                } else {
                    String type  = safe(el.getAttribute("type"));
                    String label = safe(el.getAttribute("label"));
                    String name  = safe(el.getAttribute("name"));
                    String value = safe(el.getAttribute("value"));
                    if (label.isBlank() && name.isBlank() && value.isBlank()) continue;
                    sb.append(String.format("  [%d] %s | label='%s' name='%s' value='%s'%n",
                            i++, shortType(type), label, name, value));
                }
                if (i >= 50) { sb.append("  ...(truncated)\n"); break; }
            }
            if (i == 0) sb.append("  (no interactable elements found)\n");
            return ToolResult.ok(sb.toString());
        } catch (Exception e) {
            return ToolResult.fail("Could not read screen: " + e.getMessage());
        }
    }

    private static String safe(String s)      { return s == null ? "" : s; }
    private static String shortType(String t) { return t == null ? "?" : t.replace("XCUIElementType", ""); }
    private static String shortClass(String c) {
        if (c == null) return "?";
        int dot = c.lastIndexOf('.');
        return dot >= 0 ? c.substring(dot + 1) : c;
    }
}
