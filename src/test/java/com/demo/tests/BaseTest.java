package com.demo.tests;

import com.demo.agent.LlmClient;
import com.demo.agent.TestAgent;
import com.demo.config.ConfigLoader;
import com.demo.driver.DriverFactory;
import com.demo.tools.*;
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
        Thread.sleep(Duration.ofSeconds(2).toMillis());

        LlmClient llm = new LlmClient(cfg);
        List<Tool> tools = List.of(new PerceiveTool(), new ActTool(), new AssertTool());
        agent = new TestAgent(llm, tools, 25);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quit();
    }
}
