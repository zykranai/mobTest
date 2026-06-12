package io.mobtest.agentic.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

/**
 * Ends an Appium session: terminate app (optional), quit driver, clear ThreadLocal.
 */
public final class SessionManager {

    private SessionManager() {}

    public static void release(AppiumConfig cfg, boolean terminateApp) {
        AppiumDriver driver = DriverFactory.get();
        if (driver == null) {
            DriverFactory.clearThreadLocals();
            return;
        }

        if (terminateApp && cfg != null) {
            terminateApp(driver, cfg);
        }

        quitDriver(driver);
        DriverFactory.clearThreadLocals();
    }

    private static void terminateApp(AppiumDriver driver, AppiumConfig cfg) {
        try {
            if (cfg.platform() == Platform.IOS) {
                String bundleId = cfg.bundleId();
                if (bundleId != null && !bundleId.isBlank() && driver instanceof IOSDriver ios) {
                    ios.terminateApp(bundleId);
                }
            } else {
                String pkg = cfg.appPackage();
                if (pkg != null && !pkg.isBlank() && driver instanceof AndroidDriver android) {
                    android.terminateApp(pkg);
                }
            }
        } catch (Exception e) {
            System.out.println("[mobtest] Could not terminate app: " + e.getMessage());
        }
    }

    private static void quitDriver(AppiumDriver driver) {
        try {
            driver.quit();
        } catch (Exception e) {
            System.out.println("[mobtest] Driver quit failed: " + e.getMessage());
            try {
                driver.close();
            } catch (Exception ignored) {
                // best effort
            }
        }
    }
}
