package io.mobtest.agentic.tests;

import io.mobtest.agentic.agent.LlmClient;
import io.mobtest.agentic.agent.TestAgent;
import io.mobtest.agentic.config.ConfigLoader;
import io.mobtest.agentic.driver.DeviceManager;
import io.mobtest.agentic.driver.DriverFactory;
import io.mobtest.agentic.driver.Platform;
import io.mobtest.agentic.driver.SessionManager;
import io.mobtest.agentic.reporting.AgentReporter;
import io.mobtest.agentic.support.PreflightChecker;
import io.mobtest.agentic.tools.*;
import io.qameta.allure.Step;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;
import java.util.List;

/**
 * Extend this, call {@link #runGoal(String)}, done.
 *
 * Each test gets a fresh Appium session. Teardown terminates the app and quits
 * the driver. The suite listener shuts down simulators/emulators when the run
 * was started via {@code ./scripts/run-tests.sh}.
 */
public abstract class BaseTest {

    protected TestAgent agent;
    protected ConfigLoader cfg;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        cfg = new ConfigLoader();
        PreflightChecker.validate(cfg);

        if (!cfg.runCloud()) {
            prepareLocalDevice();
        }

        DriverFactory.create(cfg.appiumConfig());
        waitForAppLaunch();

        LlmClient llm = new LlmClient(cfg);
        List<Tool> tools = List.of(new PerceiveTool(), new ActTool(), new AssertTool());
        agent = new TestAgent(llm, tools, 25);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        try {
            boolean terminate = cfg != null && cfg.cleanupDriverAfterTest();
            SessionManager.release(cfg != null ? cfg.appiumConfig() : null, terminate);
        } catch (Exception e) {
            System.out.println("[mobtest] Teardown warning: " + e.getMessage());
        }
    }

    @Step("Run agent goal: {goal}")
    protected void runGoal(String goal) {
        TestAgent.RunResult result = agent.run(goal);

        System.out.println("\n=== Agent run trace ===");
        result.steps().forEach(s -> System.out.printf(
                "  [%d] %s | %s | %s -> %s%n",
                s.step(), s.thought(), s.tool(), s.args(), s.observation()));
        System.out.println("Summary: " + result.summary());

        AgentReporter.attachRun(goal, result);
        Assert.assertTrue(result.passed(),
                "Agent did not verify the goal. Summary: " + result.summary());
    }

    private void prepareLocalDevice() {
        String device = cfg.platform() == Platform.ANDROID
                ? cfg.get("android.device.name", "emulator-5554")
                : cfg.get("ios.device.name", "iPhone 17");

        if (cfg.autoBootDevice() || cfg.deviceManagedByFramework()) {
            if (cfg.platform() == Platform.IOS) {
                DeviceManager.bootIosSimulatorIfNeeded(device);
            }
            DeviceManager.markManaged(device);
        }
    }

    private void waitForAppLaunch() {
        long seconds = cfg.platform() == Platform.ANDROID ? 4 : 2;
        new FluentWait<>(DriverFactory.get())
                .withTimeout(Duration.ofSeconds(seconds))
                .pollingEvery(Duration.ofMillis(250))
                .ignoring(Exception.class)
                .until(d -> d.getPageSource() != null && !d.getPageSource().isBlank());
    }
}
