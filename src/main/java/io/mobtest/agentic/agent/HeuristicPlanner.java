package io.mobtest.agentic.agent;

/**
 * Offline fallback brain when Ollama is unavailable.
 *
 * Uses goal keywords + current screen text to emit the same JSON tool-call
 * format the agent loop expects.
 */
public final class HeuristicPlanner {

    private HeuristicPlanner() {}

    public static String complete(String goal, String screen, String history) {
        String g = goal.toLowerCase();
        String s = screen.toLowerCase();
        String h = history.toLowerCase();

        if (containsAny(g, "order placed", "successfully") && s.contains("order placed")) {
            return finish("pass", "Order success message is visible.");
        }
        if (g.contains("sign in heading") && s.contains("sign in") && !done(h, "sign in heading")) {
            return finish("pass", "Sign In heading is visible.");
        }
        if (containsAny(g, "browse categories", "home screen") && s.contains("browse categories")) {
            return finish("pass", "Home screen with categories is visible.");
        }
        if (containsAny(g, "checkout", "place order") && s.contains("checkout") && !done(h, "tapped 'checkout'")) {
            return act("tap", "Checkout", "Checkout", "checkout_button");
        }
        if (containsAny(g, "headphones", "wireless headphones") && s.contains("wireless headphones")
                && !done(h, "tapped 'add wireless headphones'")) {
            return act("tap", "Add Wireless Headphones", "Add to Cart", "add_to_cart_e1");
        }
        if (containsAny(g, "electronics") && s.contains("electronics") && !done(h, "tapped 'electronics'")) {
            return act("tap", "Electronics", "Electronics", "category_electronics");
        }
        if (containsAny(g, "cart") && !s.contains("your cart") && shouldOpenCart(g, h) && !done(h, "tapped 'go to cart'")) {
            if (s.contains("go to cart")) return act("tap", "Go to Cart", "Go to Cart", "products_go_to_cart");
            if (s.contains("cart (")) return act("tap", "Cart", "Cart", "home_cart_button");
        }
        if (containsAny(g, "get started", "welcome", "open app", "shopmate")
                && (s.contains("get started") || s.contains("welcome_get_started")) && !done(h, "tapped 'get started'")) {
            return act("tap", "Get Started", "Get Started", "welcome_get_started");
        }
        if (wantsLogin(g) && s.contains("sign in")) {
            if (!done(h, "typed into 'email'")) {
                return type("Email", "demo@shopmate.com", "login_email_field");
            }
            if (!done(h, "typed into 'password'")) {
                return type("Password", "secret123", "login_password_field");
            }
            if (!done(h, "tapped 'sign in'")) {
                return act("tap", "Sign In", "Sign In", "login_submit_btn");
            }
        }

        if (s.contains("get started")) return act("tap", "Get Started", "Get Started", "welcome_get_started");
        if (s.contains("electronics")) return act("tap", "Electronics", "Electronics", "category_electronics");
        if (s.contains("wireless headphones")) return act("tap", "Add Wireless Headphones", "Add to Cart", "add_to_cart_e1");

        return finish("fail", "Heuristic planner could not infer the next step for this screen.");
    }

    private static boolean shouldOpenCart(String goal, String history) {
        if (goal.contains("checkout") || goal.contains("cart")) {
            return done(history, "tapped 'add wireless headphones'") || done(history, "tapped 'electronics'");
        }
        return !history.contains("cart");
    }

    private static boolean wantsLogin(String g) {
        return g.contains("demo@") || g.contains("password") || g.contains("sign in with")
                || g.contains("login") || g.contains("sign in to") || g.contains("sign in");
    }

    private static boolean done(String history, String observationSnippet) {
        return history.contains(observationSnippet.toLowerCase());
    }

    private static boolean containsAny(String text, String... needles) {
        for (String n : needles) if (text.contains(n)) return true;
        return false;
    }

    private static String act(String action, String target, String label, String accId) {
        return "{\"thought\":\"heuristic: " + action + " " + target + "\","
                + "\"tool\":\"act\",\"args\":{\"action\":\"" + action + "\","
                + "\"target\":\"" + target + "\",\"label\":\"" + label + "\","
                + "\"accessibilityId\":\"" + accId + "\"}}";
    }

    private static String type(String target, String text, String accId) {
        return "{\"thought\":\"heuristic: type into " + target + "\","
                + "\"tool\":\"act\",\"args\":{\"action\":\"type\","
                + "\"target\":\"" + target + "\",\"text\":\"" + text + "\","
                + "\"accessibilityId\":\"" + accId + "\"}}";
    }

    private static String finish(String status, String reason) {
        return "{\"thought\":\"heuristic: done\",\"tool\":\"finish\","
                + "\"args\":{\"status\":\"" + status + "\",\"reason\":\"" + reason + "\"}}";
    }
}
