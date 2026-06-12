package io.mobtest.agentic.reporting;

import io.mobtest.agentic.driver.DriverFactory;
import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public final class ScreenshotHelper {

    private ScreenshotHelper() {}

    public static void attach(String name) {
        AppiumDriver driver = DriverFactory.get();
        if (driver == null) return;
        try {
            String base64 = driver.getScreenshotAs(OutputType.BASE64);
            byte[] bytes = Base64.getDecoder().decode(base64);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(bytes), ".png");
        } catch (Exception e) {
            Allure.addAttachment(name + " (failed)", "text/plain", e.getMessage());
        }
    }
}
