package io.mobtest.agentic.tests;

import io.mobtest.agentic.agent.LlmClient;
import io.mobtest.agentic.agent.TestAgent;
import io.mobtest.agentic.config.ConfigLoader;
import io.mobtest.agentic.driver.DriverFactory;
import io.mobtest.agentic.driver.Platform;
import io.mobtest.agentic.tools.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;
import java.util.List;

/**
 * Shared setup: starts the iOS driver, wires up the agent with its tools,
 * and tears the session down after each test.
 *
 * Subclass test methods just describe a GOAL in English and let the agent
 * figure out the steps.
 */
public abstract class BaseTest {

    protected TestAgent agent;
    private ConfigLoader cfg;

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        cfg = new ConfigLoader();
        DriverFactory.create(cfg.appiumConfig());
        long waitMs = cfg.platform() == Platform.ANDROID
                ? Duration.ofSeconds(4).toMillis() : Duration.ofSeconds(2).toMillis();
        Thread.sleep(waitMs);

        LlmClient llm = new LlmClient(cfg);
        List<Tool> tools = List.of(new PerceiveTool(), new ActTool(), new AssertTool());
        agent = new TestAgent(llm, tools, 25);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quit();
    }
}
