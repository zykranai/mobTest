package io.mobtest.agentic.driver;

/**
 * Immutable Appium / device configuration for iOS or Android.
 *
 * Values are resolved from environment variables, config.properties, or defaults
 * — see {@link io.mobtest.agentic.config.ConfigLoader}.
 */
public record AppiumConfig(
        Platform platform,
        String serverUrl,
        String platformVersion,
        String deviceName,
        String appPath,
        String bundleId,
        String appPackage,
        String appActivity,
        boolean cloudRun
) {}
