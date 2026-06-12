package io.mobtest.agentic.reporting;

import io.mobtest.agentic.agent.TestAgent;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public final class AgentReporter {

    private AgentReporter() {}

    public static void attachRun(String goal, TestAgent.RunResult result) {
        var trace = new StringBuilder();
        trace.append("GOAL: ").append(goal).append("\n\n");
        result.steps().forEach(s -> trace.append(String.format(
                "[%d] thought=%s | tool=%s | args=%s | success=%s%n  -> %s%n%n",
                s.step(), s.thought(), s.tool(), s.args(), s.success(), s.observation())));
        trace.append("SUMMARY: ").append(result.summary()).append("\n");
        trace.append("PASSED: ").append(result.passed()).append("\n");

        Allure.addAttachment("Agent step trace", "text/plain",
                new ByteArrayInputStream(trace.toString().getBytes(StandardCharsets.UTF_8)),
                ".txt");

        if (!result.passed()) {
            ScreenshotHelper.attach("Agent goal failed");
        }
    }
}
