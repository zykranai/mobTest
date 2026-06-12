package io.mobtest.agentic.driver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Boots and shuts down local simulators/emulators.
 *
 * We only shut down devices the framework started (or the suite explicitly
 * asked to manage). That way a developer's already-open Simulator is left alone.
 */
public final class DeviceManager {

    private static final Set<String> MANAGED_DEVICES = ConcurrentHashMap.newKeySet();

    private DeviceManager() {}

    /** Remember a device name/serial so we can shut it down later. */
    public static void markManaged(String deviceName) {
        if (deviceName != null && !deviceName.isBlank()) {
            MANAGED_DEVICES.add(deviceName.trim());
        }
    }

    public static void bootIosSimulatorIfNeeded(String deviceName) {
        if (deviceName == null || deviceName.isBlank()) return;
        if (isIosSimulatorBooted(deviceName)) return;
        int code = runQuiet(30, "xcrun", "simctl", "boot", deviceName);
        if (code == 0) {
            markManaged(deviceName);
            log("Booted iOS simulator: " + deviceName);
        }
    }

    /**
     * Shut down devices used during this test run.
     * Skipped for cloud runs — BrowserStack owns the device lifecycle.
     */
    public static void shutdownAfterSuite(AppiumConfig cfg) {
        if (cfg == null || cfg.cloudRun()) return;

        if (cfg.platform() == Platform.IOS) {
            shutdownIosDevices();
        } else {
            shutdownAndroidDevices(cfg.deviceName());
        }
        MANAGED_DEVICES.clear();
    }

    private static void shutdownIosDevices() {
        if (MANAGED_DEVICES.isEmpty()) {
            log("No managed simulators — leaving devices as-is.");
            return;
        }
        for (String name : MANAGED_DEVICES) {
            runQuiet(15, "xcrun", "simctl", "shutdown", name);
            log("Shut down iOS simulator: " + name);
        }
    }

    private static void shutdownAndroidDevices(String configuredDevice) {
        if (MANAGED_DEVICES.isEmpty()) {
            log("No managed emulators — leaving devices as-is.");
            return;
        }
        String serial = resolveAndroidSerial(configuredDevice);
        if (serial == null || !isAndroidEmulator(serial)) {
            log("Skipping shutdown — not an emulator serial: " + serial);
            return;
        }
        if (!MANAGED_DEVICES.contains(serial) && !MANAGED_DEVICES.contains(configuredDevice)) {
            log("Skipping shutdown — emulator was not started by the framework.");
            return;
        }
        runQuiet(20, "adb", "-s", serial, "emu", "kill");
        log("Stopped Android emulator: " + serial);
    }

    static boolean isAndroidEmulator(String serial) {
        return serial != null && serial.startsWith("emulator-");
    }

    private static String resolveAndroidSerial(String configuredDevice) {
        if (configuredDevice != null && configuredDevice.startsWith("emulator-")) {
            return configuredDevice.trim();
        }
        String listing = runCapture(10, "adb", "devices");
        if (listing != null) {
            for (String line : listing.split("\n")) {
                if (line.contains("\temulator") && line.contains("device")) {
                    return line.split("\\s+")[0];
                }
            }
        }
        return configuredDevice;
    }

    private static boolean isIosSimulatorBooted(String deviceName) {
        String listing = runCapture(10, "xcrun", "simctl", "list", "devices", "booted");
        return listing != null && listing.contains(deviceName);
    }

    private static int runQuiet(long timeoutSec, String... cmd) {
        try {
            Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            boolean finished = p.waitFor(timeoutSec, TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                return -1;
            }
            return p.exitValue();
        } catch (Exception e) {
            log("Command failed (" + String.join(" ", cmd) + "): " + e.getMessage());
            return -1;
        }
    }

    private static String runCapture(long timeoutSec, String... cmd) {
        try {
            Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            boolean finished = p.waitFor(timeoutSec, TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                return null;
            }
            try (var reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                return sb.toString();
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static void log(String msg) {
        System.out.println("[mobtest] " + msg);
    }
}
