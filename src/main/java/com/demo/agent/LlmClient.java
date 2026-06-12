package com.demo.agent;

import com.demo.config.ConfigLoader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.util.concurrent.TimeUnit;

/**
 * Thin wrapper around a LOCAL Ollama model — the agent's free, offline brain.
 *
 * Talks to Ollama's chat API at http://localhost:11434/api/chat. No API key,
 * no cloud, no cost. Pull a model once with: `ollama pull llama3.1`.
 *
 * If Ollama isn't running or the model isn't pulled, complete() returns a
 * clean "finish" so the run ends gracefully instead of throwing.
 */
public class LlmClient {

    private final OkHttpClient http = new OkHttpClient.Builder()
            .callTimeout(120, TimeUnit.SECONDS).build();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String endpoint;
    private final String model;

    public LlmClient(ConfigLoader cfg) {
        this.endpoint = cfg.get("ollama.endpoint", "http://localhost:11434/api/chat");
        this.model    = cfg.get("ollama.model", "llama3.1");
        this.preferHeuristic = cfg.heuristicOnly();
    }

    private volatile boolean preferHeuristic = false;

    public boolean isOffline() { return preferHeuristic; }

    /** Send a system + user prompt to the local model, return its text reply. */
    public String complete(String systemPrompt, String userPrompt) {
        if (preferHeuristic) {
            return heuristic(userPrompt);
        }
        try {
            var body = mapper.createObjectNode();
            body.put("model", model);
            body.put("stream", false);
            body.put("format", "json");
            var messages = body.putArray("messages");
            messages.addObject().put("role", "system").put("content", systemPrompt);
            messages.addObject().put("role", "user").put("content", userPrompt);

            Request req = new Request.Builder()
                    .url(endpoint)
                    .post(RequestBody.create(mapper.writeValueAsBytes(body),
                            MediaType.parse("application/json")))
                    .build();

            try (Response resp = http.newCall(req).execute()) {
                if (!resp.isSuccessful()) {
                    preferHeuristic = true;
                    return heuristic(userPrompt);
                }
                String raw = resp.body() != null ? resp.body().string() : "";
                JsonNode root = mapper.readTree(raw);
                JsonNode content = root.path("message").path("content");
                if (!content.isMissingNode() && !content.asText().isBlank()) {
                    return content.asText();
                }
                preferHeuristic = true;
                return heuristic(userPrompt);
            }
        } catch (Exception e) {
            preferHeuristic = true;
            return heuristic(userPrompt);
        }
    }

    private String heuristic(String userPrompt) {
        String goal = extractSection(userPrompt, "GOAL:");
        String screen = extractSection(userPrompt, "CURRENT SCREEN:");
        String history = extractSection(userPrompt, "HISTORY:");
        return HeuristicPlanner.complete(goal, screen, history);
    }

    private static String extractSection(String prompt, String marker) {
        int start = prompt.indexOf(marker);
        if (start < 0) return "";
        start += marker.length();
        int end = prompt.indexOf("\n\n", start);
        return (end < 0 ? prompt.substring(start) : prompt.substring(start, end)).trim();
    }
}
