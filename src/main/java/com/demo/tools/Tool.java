package com.demo.tools;

import java.util.Map;

/**
 * A Tool is a single capability the agent can invoke on the app, e.g.
 * "tap an element", "type text", "read the screen".
 *
 * Keeping each capability behind this interface is what makes the agent
 * loop simple: the LLM picks a tool name + arguments, the framework runs
 * it, and feeds the result back. Adding a new capability = adding a Tool.
 */
public interface Tool {

    /** Stable name the LLM refers to, e.g. "tap", "type", "read_screen". */
    String name();

    /** One-line description shown to the LLM so it knows when to use this. */
    String description();

    /** Execute the tool with the given arguments. */
    ToolResult execute(Map<String, Object> args);

    record ToolResult(boolean success, String observation) {
        public static ToolResult ok(String observation)  { return new ToolResult(true, observation); }
        public static ToolResult fail(String observation) { return new ToolResult(false, observation); }
    }
}
