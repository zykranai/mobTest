package io.mobtest.agentic.config;

import io.mobtest.agentic.driver.AppiumConfig;
import io.mobtest.agentic.driver.Platform;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Loads settings with this precedence (highest wins):
 * JVM system property → environment variable → config.local.properties → config.properties.
 */
public class ConfigLoader {

    private final Properties props = new Properties();

    public ConfigLoader() {
        loadFile("config.properties");
        overlayFile("config.local.properties");
    }

    private void loadFile(String name) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(name)) {
            if (in != null) props.load(in);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read " + name, e);
        }
    }

    /** Local overrides sit on top of committed defaults without replacing the whole file. */
    private void overlayFile(String name) {
        Properties overlay = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(name)) {
            if (in == null) return;
            overlay.load(in);
            overlay.forEach((key, value) -> props.put(key, value));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read " + name, e);
        }
    }

    public String get(String key, String def) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;
        String env = System.getenv(toEnv(key));
        if (env != null && !env.isBlank()) return env;
        return props.getProperty(key, def);
    }

    public Platform platform() {
        String prop = System.getProperty("platform");
        if (prop != null && !prop.isBlank()) return Platform.from(prop);
        return Platform.from(get("platform", "ios"));
    }

    public AppiumConfig appiumConfig() {
        Platform platform = platform();
        if (platform == Platform.ANDROID) {
            return new AppiumConfig(
                    platform,
                    get("appium.server.url", "http://127.0.0.1:4723"),
                    get("android.platform.version", "16"),
                    get("android.device.name", "emulator-5554"),
                    resolvePath(get("android.app.path",
                            "apps/shopmate-android/dist/shopmate-debug.apk")),
                    "",
                    get("android.app.package", "com.mobtest.shopmate"),
                    get("android.app.activity", "com.mobtest.shopmate.MainActivity"),
                    runCloud()
            );
        }
        return new AppiumConfig(
                platform,
                get("appium.server.url", "http://127.0.0.1:4723"),
                get("ios.platform.version", "26.5"),
                get("ios.device.name", "iPhone 17"),
                resolvePath(get("ios.app.path",
                        "apps/shopmate-ios/dist/ShopMate.app")),
                get("ios.bundle.id", "com.mobtest.shopmate"),
                "",
                "",
                runCloud()
        );
    }

    public Path resolvedAppPath() {
        String configured = platform() == Platform.ANDROID
                ? get("android.app.path", "apps/shopmate-android/dist/shopmate-debug.apk")
                : get("ios.app.path", "apps/shopmate-ios/dist/ShopMate.app");
        Path path = Path.of(resolvePath(configured));
        return path.isAbsolute() ? path : Path.of(System.getProperty("user.dir")).resolve(path).normalize();
    }

    private String resolvePath(String configured) {
        if (configured == null || configured.isBlank()) return "";
        Path path = Path.of(configured);
        if (path.isAbsolute()) return configured;
        return Path.of(System.getProperty("user.dir")).resolve(path).normalize().toString();
    }

    public boolean runCloud() {
        String prop = System.getProperty("run.cloud");
        if (prop != null && !prop.isBlank()) return Boolean.parseBoolean(prop);
        return Boolean.parseBoolean(get("run.cloud", "false"));
    }

    public String ollamaEndpoint() { return get("ollama.endpoint", "http://localhost:11434/api/chat"); }
    public String ollamaModel()     { return get("ollama.model", "llama3.1"); }

    public boolean heuristicOnly() {
        return Boolean.parseBoolean(get("agent.heuristic.only", "false"));
    }

    public boolean shutdownDevicesAfterSuite() {
        return Boolean.parseBoolean(get("device.shutdown.after.suite", "true"));
    }

    public boolean autoBootDevice() {
        return Boolean.parseBoolean(get("device.auto.boot", "false"));
    }

    /** Set by run-tests.sh when the script boots a simulator/emulator. */
    public boolean deviceManagedByFramework() {
        return Boolean.parseBoolean(get("device.managed.by.framework", "false"));
    }

    public boolean cleanupDriverAfterTest() {
        return Boolean.parseBoolean(get("driver.cleanup.after.test", "true"));
    }

    private String toEnv(String key) {
        return key.toUpperCase().replace('.', '_');
    }
}
