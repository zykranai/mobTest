package io.mobtest.agentic.config;

import io.mobtest.agentic.driver.AppiumConfig;
import io.mobtest.agentic.driver.Platform;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Loads configuration: environment variable → config.local.properties → config.properties → default.
 *
 * Copy {@code config.properties.example} to {@code config.local.properties} and edit paths
 * when swapping in your own app.
 */
public class ConfigLoader {

    private final Properties props = new Properties();

    public ConfigLoader() {
        loadFile("config.local.properties");
        loadFile("config.properties");
    }

    private void loadFile(String name) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(name)) {
            if (in != null) props.load(in);
        } catch (Exception ignored) {
            // optional file
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
                    get("android.platform.version", "14"),
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
                get("ios.platform.version", "17.0"),
                get("ios.device.name", "iPhone 17"),
                resolvePath(get("ios.app.path",
                        "apps/shopmate-ios/dist/ShopMate.app")),
                get("ios.bundle.id", "com.mobtest.shopmate"),
                "",
                "",
                runCloud()
        );
    }

    private String resolvePath(String configured) {
        if (configured == null || configured.isBlank()) return "";
        Path path = Path.of(configured);
        if (path.isAbsolute()) return configured;
        Path resolved = Path.of(System.getProperty("user.dir")).resolve(path).normalize();
        return Files.exists(resolved) ? resolved.toString() : resolved.toString();
    }

    public boolean runCloud() {
        String prop = System.getProperty("run.cloud");
        if (prop != null && !prop.isBlank()) return Boolean.parseBoolean(prop);
        return Boolean.parseBoolean(get("run.cloud", "false"));
    }

    public String ollamaEndpoint() { return get("ollama.endpoint", "http://localhost:11434/api/chat"); }
    public String ollamaModel()     { return get("ollama.model", "llama3.1"); }

    public boolean heuristicOnly() {
        return Boolean.parseBoolean(get("agent.heuristic.only",
                System.getProperty("agent.heuristic.only", "false")));
    }

    private String toEnv(String key) {
        return key.toUpperCase().replace('.', '_');
    }
}
