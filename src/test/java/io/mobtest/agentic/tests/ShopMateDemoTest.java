package io.mobtest.agentic.tests;

import io.qameta.allure.Description;
import org.testng.annotations.Test;

/**
 * Demo tests for the bundled ShopMate app.
 *
 * Each test is just a goal in English — no locators here. For your own app,
 * copy {@link YourAppAgentTest} and point config.local.properties at your build.
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
}
