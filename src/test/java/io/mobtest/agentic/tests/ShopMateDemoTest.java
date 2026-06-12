package io.mobtest.agentic.tests;

import io.mobtest.agentic.agent.TestAgent;
import io.mobtest.agentic.reporting.AgentReporter;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Agentic tests against the ShopMate sample iOS app.
 *
 * Each test is a plain-English goal — no locators or waits in the test code.
 * The agent perceives the live screen, decides actions, and self-heals via
 * ActTool's locator cascade when labels or ids drift.
 */
public class ShopMateDemoTest extends BaseTest {

    @Test(description = "Agent completes welcome to home login flow")
    @Description("Tap Get Started, sign in, and verify Home categories are visible.")
    public void agentSignsInToShopMate() {
        runGoal("In the ShopMate app, tap 'Get Started', sign in with email "
                + "demo@shopmate.com and password secret123, then verify the Home "
                + "screen with browse categories is visible.");
    }

    @Test(description = "Agent adds headphones and checks out")
    @Description("Full e-commerce flow: login, Electronics, add headphones, checkout.")
    public void agentCompletesCheckoutFlow() {
        runGoal("In ShopMate, sign in with demo@shopmate.com / secret123, open "
                + "Electronics, add Wireless Headphones to cart, open the cart, "
                + "tap Checkout, and verify 'Order Placed Successfully!' is shown.");
    }

    @Test(description = "Self-healing tap using label instead of accessibility id")
    @Description("Agent finds 'Get Started' by label even without accessibilityId hint.")
    public void agentSelfHealsWelcomeTap() {
        runGoal("On the ShopMate welcome screen, tap the button labeled 'Get Started' "
                + "and verify the Sign In heading appears.");
    }

    @Step("Run agent goal: {goal}")
    private void runGoal(String goal) {
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
}
