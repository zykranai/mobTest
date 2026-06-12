package io.mobtest.agentic.tools;

import io.mobtest.agentic.driver.DriverFactory;
import io.mobtest.agentic.driver.Platform;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

final class LocatorHelper {

    private LocatorHelper() {}

    static Located locate(AppiumDriver driver, Map<String, Object> args) {
        String accId = str(args, "accessibilityId");
        String label = str(args, "label");
        String target = str(args, "target");
        List<Supplier<Located>> strategies = new ArrayList<>();

        if (DriverFactory.platform() == Platform.ANDROID) {
            if (!accId.isBlank()) {
                strategies.add(() -> find(driver, AppiumBy.accessibilityId(accId), "accessibility-id"));
                strategies.add(() -> find(driver, By.xpath("//*[@content-desc=" + xp(accId) + "]"), "content-desc-id"));
                strategies.add(() -> find(driver, By.xpath("//*[contains(@content-desc," + xp(accId) + ")]"), "partial-content-desc-id"));
            }
            if (!label.isBlank()) {
                strategies.add(() -> find(driver, By.xpath("//*[@content-desc=" + xp(label) + "]"), "content-desc"));
                strategies.add(() -> find(driver, By.xpath("//*[contains(@content-desc," + xp(label) + ")]"), "partial-content-desc"));
                strategies.add(() -> find(driver, By.xpath("//*[@text=" + xp(label) + "]"), "exact-text"));
            }
            if (!target.isBlank()) {
                strategies.add(() -> find(driver, By.xpath("//*[contains(@content-desc," + xp(target) + ")]"), "target-content-desc"));
                strategies.add(() -> find(driver, By.xpath("//*[contains(@text," + xp(target) + ")]"), "target-text"));
            }
        } else {
            if (!accId.isBlank()) {
                strategies.add(() -> find(driver, By.id(accId), "accessibilityId"));
                strategies.add(() -> find(driver, By.xpath("//*[@name=" + xp(accId) + "]"), "accessibility-name"));
            }
            if (!label.isBlank()) {
                strategies.add(() -> find(driver, By.xpath("//*[@label=" + xp(label) + "]"), "exact-label"));
                strategies.add(() -> find(driver, By.xpath("//*[contains(@label," + xp(label) + ")]"), "partial-label"));
                strategies.add(() -> find(driver, By.xpath("//*[@name=" + xp(label) + "]"), "exact-name"));
            }
            if (!target.isBlank()) {
                strategies.add(() -> find(driver, By.xpath("//*[contains(@label," + xp(target) + ")]"), "target-partial-label"));
                strategies.add(() -> find(driver, By.xpath("//*[contains(@name," + xp(target) + ")]"), "target-partial-name"));
                strategies.add(() -> find(driver, By.xpath("//*[contains(@value," + xp(target) + ")]"), "target-partial-value"));
            }
        }

        for (Supplier<Located> strategy : strategies) {
            Located located = strategy.get();
            if (located != null) return located;
        }
        return null;
    }

    static WebElement first(AppiumDriver driver, By by) {
        List<WebElement> els = driver.findElements(by);
        for (WebElement el : els) {
            try {
                if (el.isDisplayed()) return el;
            } catch (Exception ignored) {
                // stale element
            }
        }
        return null;
    }

    static String xp(String s) {
        if (!s.contains("'")) return "'" + s + "'";
        return "concat('" + s.replace("'", "',\"'\",'") + "')";
    }

    private static Located find(AppiumDriver driver, By by, String strategy) {
        WebElement e = first(driver, by);
        return e != null ? new Located(e, strategy) : null;
    }

    private static String str(Map<String, Object> a, String k) {
        Object v = a.get(k);
        return v == null ? "" : v.toString();
    }

    record Located(WebElement element, String strategy) {}
}
