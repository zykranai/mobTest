package com.demo.agent;

import com.demo.tools.Tool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The autonomous agent that drives the app.
 *
 * Given a plain-English goal (e.g. "Open Settings, go to General, and verify
 * 'About' is shown"), it runs a perceive -> decide -> act -> verify loop:
 *
 *   1. PERCEIVE  read the current screen (read_screen tool)
 *   2. DECIDE    ask the LLM for the single next tool call as JSON
 *   3. ACT       execute that tool against the live app
 *   4. OBSERVE   feed the result back and repeat
 *
 * The loop ends when the LLM emits the "finish" tool or maxSteps is hit.
 * Each step is recorded so the run can be turned into a report.
 */
public class TestAgent {

    private final LlmClient llm;
    private final Map<String, Tool> tools = new LinkedHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final int maxSteps;

    public TestAgent(LlmClient llm, List<Tool> toolList, int maxSteps) {
        this.llm = llm;
        this.maxSteps = maxSteps;
        for (Tool t : toolList) tools.put(t.name(), t);
    }

    public record StepRecord(int step, String thought, String tool, String args, String observation, boolean success) {}

    public record RunResult(boolean passed, String summary, List<StepRecord> steps) {}

    public RunResult run(String goal) {
        var history = new StringBuilder();
        var records = new java.util.ArrayList<StepRecord>();
        boolean passed = false;
        String summary = "Max steps reached without finishing.";

        Tool perceive = tools.get("read_screen");

        for (int step = 1; step <= maxSteps; step++) {
            String screen = "";
            if (perceive != null) {
                screen = perceive.execute(Map.of()).observation();
            }

            String prompt = buildUserPrompt(goal, history.toString(), screen);
            String response = llm.complete(systemPrompt(), prompt);

            JsonNode decision = parse(response);
            String thought = decision.path("thought").asText("");
            String toolName = decision.path("tool").asText("finish");
            Map<String, Object> args = toArgs(decision.path("args"));

            if (toolName.equals("finish")) {
                String status = args.getOrDefault("status", "").toString();
                if (status.isBlank()) {
                    history.append("Step ").append(step).append(": invalid finish (no status), continuing.\n");
                    continue;
                }
                passed = "pass".equalsIgnoreCase(status);
                summary = args.getOrDefault("reason", "Agent finished.").toString();
                records.add(new StepRecord(step, thought, "finish", args.toString(), summary, passed));
                break;
            }

            Tool tool = tools.get(toolName);
            Tool.ToolResult result = (tool != null)
                    ? tool.execute(args)
                    : Tool.ToolResult.fail("Unknown tool: " + toolName);

            records.add(new StepRecord(step, thought, toolName, args.toString(),
                    result.observation(), result.success()));

            history.append("Step ").append(step).append(": ")
                   .append(toolName).append(" -> ").append(result.observation()).append("\n");
        }
        return new RunResult(passed, summary, records);
    }

    private String systemPrompt() {
        var sb = new StringBuilder();
        sb.append("You are an autonomous mobile QA agent driving an iOS app via tools.\n");
        sb.append("On each turn, respond with ONE tool call as strict JSON: ");
        sb.append("{\"thought\":\"...\",\"tool\":\"<name>\",\"args\":{...}}.\n");
        sb.append("Available tools:\n");
        tools.values().forEach(t -> sb.append("  - ").append(t.name())
                .append(": ").append(t.description()).append("\n"));
        sb.append("  - finish: end the run. args {status:pass|fail, reason:<text>}.\n");
        sb.append("Rules:\n");
        sb.append("- Screen state is provided each turn; pick the single best next action.\n");
        sb.append("- Prefer accessibilityId when visible, else label/target text.\n");
        sb.append("- For login: type email and password before tapping Sign In.\n");
        sb.append("- For ShopMate: Welcome -> Login -> Home -> Category -> Add to Cart -> Cart -> Checkout.\n");
        sb.append("- Use assert_visible to verify text before calling finish with status pass.\n");
        sb.append("- When the goal is verified, call finish with status pass or fail.\n");
        return sb.toString();
    }

    private String buildUserPrompt(String goal, String history, String screen) {
        return "GOAL: " + goal + "\n\nCURRENT SCREEN:\n"
                + (screen.isBlank() ? "(not available)" : screen)
                + "\n\nHISTORY:\n"
                + (history.isBlank() ? "(none yet)" : history)
                + "\n\nReturn the next tool call as JSON.";
    }

    private JsonNode parse(String response) {
        try {
            String cleaned = response.replaceAll("(?s)```json", "")
                    .replaceAll("```", "").trim();
            int start = cleaned.indexOf('{');
            int end = cleaned.lastIndexOf('}');
            if (start >= 0 && end > start) cleaned = cleaned.substring(start, end + 1);
            JsonNode node = mapper.readTree(cleaned);
            if (node.has("tool")) return node;
        } catch (Exception ignored) {
            // fall through to heuristic JSON
        }
        return mapper.createObjectNode()
                .put("thought", "parse-fallback")
                .put("tool", "read_screen");
    }

    private Map<String, Object> toArgs(JsonNode node) {
        Map<String, Object> args = new HashMap<>();
        if (node != null && node.isObject()) {
            node.fields().forEachRemaining(e -> {
                JsonNode v = e.getValue();
                args.put(e.getKey(), v.isValueNode() ? v.asText() : v.toString());
            });
        }
        return args;
    }
}
