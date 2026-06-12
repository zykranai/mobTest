package io.mobtest.agentic.support;

import io.mobtest.agentic.config.ConfigLoader;
import io.mobtest.agentic.driver.DeviceManager;
import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * Runs once when a TestNG suite finishes — shuts down simulators/emulators
 * so they don't keep eating RAM after mvn test returns.
 */
public class SuiteLifecycleListener implements ISuiteListener {

    @Override
    public void onFinish(ISuite suite) {
        ConfigLoader cfg = new ConfigLoader();
        if (!cfg.shutdownDevicesAfterSuite() || cfg.runCloud()) {
            return;
        }
        DeviceManager.shutdownAfterSuite(cfg.appiumConfig());
    }
}
