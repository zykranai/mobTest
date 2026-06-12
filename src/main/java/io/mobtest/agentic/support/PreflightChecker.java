package io.mobtest.agentic.support;

import io.mobtest.agentic.config.ConfigLoader;
import io.mobtest.agentic.driver.AppiumConfig;
import io.mobtest.agentic.driver.Platform;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;

/**
 * Fails fast with a readable message before Appium session creation.
 */
public final class PreflightChecker {

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private PreflightChecker() {}

    public static void validate(ConfigLoader cfg) {
        if (cfg.runCloud()) return;

        AppiumConfig appium = cfg.appiumConfig();
        var appPath = cfg.resolvedAppPath();

        if (!Files.exists(appPath)) {
            String hint = cfg.platform() == Platform.ANDROID
                    ? "./scripts/build-demo-app.sh android"
                    : "./scripts/build-demo-app.sh ios";
            throw new IllegalStateException(
                    "App binary not found: " + appPath + "\n"
                    + "Build the demo app first: " + hint + "\n"
                    + "Or point ios.app.path / android.app.path in config.local.properties at your build.");
        }

        ping(appium.serverUrl() + "/status", "Appium",
                "Start Appium: appium  (expected at " + appium.serverUrl() + ")");

        if (!cfg.heuristicOnly()) {
            String ollamaBase = cfg.ollamaEndpoint().replace("/api/chat", "");
            ping(ollamaBase + "/api/tags", "Ollama",
                    "Start Ollama: ollama serve  &&  ollama pull " + cfg.ollamaModel());
        }
    }

    private static void ping(String url, String service, String hint) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<Void> resp = HTTP.send(req, HttpResponse.BodyHandlers.discarding());
            if (resp.statusCode() >= 200 && resp.statusCode() < 500) return;
        } catch (Exception ignored) {
            // fall through
        }
        throw new IllegalStateException(service + " is not reachable at " + url + "\n" + hint);
    }
}
