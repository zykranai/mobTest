package com.demo.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Builds and holds the Appium driver for iOS (XCUITest) or Android (UiAutomator2).
 */
public class DriverFactory {

    private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<Platform> PLATFORM = new ThreadLocal<>();

    public static AppiumDriver create(AppiumConfig cfg) {
        try {
            AppiumDriver driver = cfg.platform() == Platform.ANDROID
                    ? createAndroid(cfg)
                    : createIos(cfg);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            DRIVER.set(driver);
            PLATFORM.set(cfg.platform());
            return driver;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Appium driver. Is Appium running?", e);
        }
    }

    private static IOSDriver createIos(AppiumConfig cfg) throws Exception {
        XCUITestOptions options = new XCUITestOptions()
                .setPlatformName("iOS")
                .setAutomationName("XCUITest")
                .setNewCommandTimeout(Duration.ofSeconds(120));

        String hubUrl;
        if (cfg.cloudRun()) {
            options.setPlatformVersion("17")
                   .setDeviceName("iPhone 15")
                   .setBundleId(cfg.bundleId());
            options.setCapability("bstack:options", browserStackOptions("Agentic iOS demo"));
            hubUrl = "https://hub.browserstack.com/wd/hub";
        } else {
            options.setPlatformVersion(cfg.platformVersion())
                   .setDeviceName(cfg.deviceName());
            if (cfg.appPath() != null && !cfg.appPath().isBlank()) {
                options.setApp(cfg.appPath());
            } else {
                options.setBundleId(cfg.bundleId());
            }
            hubUrl = cfg.serverUrl();
        }
        return new IOSDriver(new URL(hubUrl), options);
    }

    private static AndroidDriver createAndroid(AppiumConfig cfg) throws Exception {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setAutomationName("UiAutomator2")
                .setNewCommandTimeout(Duration.ofSeconds(120))
                .setPlatformVersion(cfg.platformVersion())
                .setDeviceName(cfg.deviceName());

        String hubUrl;
        if (cfg.cloudRun()) {
            options.setAppPackage(cfg.appPackage())
                   .setAppActivity(cfg.appActivity());
            options.setCapability("bstack:options", browserStackOptions("Agentic Android demo"));
            hubUrl = "https://hub.browserstack.com/wd/hub";
        } else {
            if (cfg.appPath() != null && !cfg.appPath().isBlank()) {
                options.setApp(cfg.appPath());
            }
            options.setAppPackage(cfg.appPackage())
                   .setAppActivity(cfg.appActivity());
            hubUrl = cfg.serverUrl();
        }
        return new AndroidDriver(new URL(hubUrl), options);
    }

    private static Map<String, Object> browserStackOptions(String sessionName) {
        Map<String, Object> bstack = new HashMap<>();
        bstack.put("userName", requireEnv("BROWSERSTACK_USERNAME"));
        bstack.put("accessKey", requireEnv("BROWSERSTACK_ACCESS_KEY"));
        bstack.put("sessionName", sessionName);
        bstack.put("projectName", "Agentic AI Mobile Automation");
        return bstack;
    }

    private static String requireEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Cloud run requires " + name + " as an environment variable.");
        }
        return value;
    }

    public static AppiumDriver get() {
        return DRIVER.get();
    }

    public static Platform platform() {
        Platform p = PLATFORM.get();
        return p == null ? Platform.IOS : p;
    }

    public static void quit() {
        AppiumDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
            PLATFORM.remove();
        }
    }
}
