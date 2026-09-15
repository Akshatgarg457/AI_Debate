package com.project.debatepartner.controller;

import com.project.debatepartner.model.AIDebate;
import com.project.debatepartner.repository.AIDebateRepository;
import com.project.debatepartner.service.GeminiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DebateController {

    @Autowired
    private AIDebateRepository aiDebateRepository;

    @Autowired
    private GeminiService geminiService;

    // =========================================================
    // AI DEBATE - GET AI RESPONSE
    // =========================================================

    @PostMapping("/debate")
    public String handleDebate(
            @RequestBody Map<String, String> request) {

        String userArg = request.get("argument");
        String topic = request.get("topic");
        String stance = request.getOrDefault(
                "stance",
                "for"
        );

        if (userArg == null || userArg.trim().isEmpty()) {
            return "Please enter an argument.";
        }

        if (topic == null || topic.trim().isEmpty()) {
            return "Topic is required.";
        }

        return geminiService.getDebateResponse(
                topic,
                userArg,
                stance
        );
    }

    // =========================================================
    // START NEW AI DEBATE
    // =========================================================

    @PostMapping("/ai-debate/start")
    public AIDebate startAIDebate(
            @RequestParam String username,
            @RequestParam String topic,
            @RequestParam(defaultValue = "FOR") String stance) {

        AIDebate debate = new AIDebate();

        debate.setUsername(username);
        debate.setTopic(topic);
        debate.setUserStance(stance.toUpperCase());
        debate.setStatus("ACTIVE");

        return aiDebateRepository.save(debate);
    }

    // =========================================================
    // SAVE USER ARGUMENT
    // =========================================================

    @PostMapping("/ai-debate/{debateId}/user-message")
    public AIDebate saveUserArgument(
            @PathVariable String debateId,
            @RequestParam String username,
            @RequestParam String message) {

        AIDebate debate =
                aiDebateRepository
                        .findByIdAndUsername(
                                debateId,
                                username
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "AI debate not found."
                                )
                        );

        if ("COMPLETED".equals(debate.getStatus())) {
            throw new IllegalStateException(
                    "Completed debates cannot be modified."
            );
        }

        debate.addMessage(
                "USER",
                message
        );

        return aiDebateRepository.save(debate);
    }

    // =========================================================
    // SAVE AI ARGUMENT
    // =========================================================

    @PostMapping("/ai-debate/{debateId}/ai-message")
    public AIDebate saveAIArgument(
            @PathVariable String debateId,
            @RequestParam String username,
            @RequestParam String message) {

        AIDebate debate =
                aiDebateRepository
                        .findByIdAndUsername(
                                debateId,
                                username
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "AI debate not found."
                                )
                        );

        if ("COMPLETED".equals(debate.getStatus())) {
            throw new IllegalStateException(
                    "Completed debates cannot be modified."
            );
        }

        debate.addMessage(
                "AI",
                message
        );

        return aiDebateRepository.save(debate);
    }

    // =========================================================
    // END AI DEBATE
    // =========================================================

    @PostMapping("/ai-debate/{debateId}/end")
    public AIDebate endAIDebate(
            @PathVariable String debateId,
            @RequestParam String username,
            @RequestParam String winner,
            @RequestParam String result,
            @RequestParam(required = false) Integer userScore,
            @RequestParam(required = false) Integer aiScore) {

        AIDebate debate =
                aiDebateRepository
                        .findByIdAndUsername(
                                debateId,
                                username
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "AI debate not found."
                                )
                        );

        debate.completeDebate(
                winner,
                result,
                userScore,
                aiScore
        );

        return aiDebateRepository.save(debate);
    }

    // =========================================================
    // OLD SAVE DEBATE ENDPOINT
    // =========================================================
    // Kept temporarily so your existing frontend does not
    // immediately break while we migrate it.

    @PostMapping("/saveDebate")
    public String saveDebate(
            @RequestParam String username,
            @RequestParam String topic,
            @RequestParam String userArgument,
            @RequestParam String aiArgument) {

        AIDebate debate = new AIDebate();

        debate.setUsername(username);
        debate.setTopic(topic);
        debate.setUserStance("FOR");

        // -----------------------------------------------------
        // Save user argument
        // -----------------------------------------------------

        debate.addMessage(
                "USER",
                userArgument
        );

        // -----------------------------------------------------
        // Save AI argument
        // -----------------------------------------------------

        debate.addMessage(
                "AI",
                aiArgument
        );

        // -----------------------------------------------------
        // Simple winner logic
        // -----------------------------------------------------

        if (userArgument.length() > aiArgument.length()) {

            debate.completeDebate(
                    "User",
                    "User won based on argument length.",
                    null,
                    null
            );

        } else if (aiArgument.length() > userArgument.length()) {

            debate.completeDebate(
                    "AI",
                    "AI won based on argument length.",
                    null,
                    null
            );

        } else {

            debate.completeDebate(
                    "DRAW",
                    "Both arguments had equal length.",
                    null,
                    null
            );
        }

        aiDebateRepository.save(debate);

        return "Saved";
    }

    // =========================================================
    // AI DEBATE HISTORY
    // =========================================================

    @GetMapping("/history")
    public List<AIDebate> getHistory(
            @RequestParam String username) {

        return aiDebateRepository
                .findByUsernameOrderByCreatedAtDesc(
                        username
                );
    }

    // =========================================================
    // GET ONE AI DEBATE
    // =========================================================

    @GetMapping("/ai-debate/{debateId}")
    public AIDebate getAIDebate(
            @PathVariable String debateId,
            @RequestParam String username) {

        return aiDebateRepository
                .findByIdAndUsername(
                        debateId,
                        username
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "AI debate not found."
                        )
                );
    }

    // =========================================================
    // AI DEBATE STATS
    // =========================================================

    @GetMapping("/stats")
    public Map<String, Integer> getStats(
            @RequestParam String username) {

        List<AIDebate> debates =
                aiDebateRepository
                        .findByUsernameOrderByCreatedAtDesc(
                                username
                        );

        int wins = 0;
        int losses = 0;
        int draws = 0;

        for (AIDebate debate : debates) {

            String winner = debate.getWinner();

            if ("User".equalsIgnoreCase(winner)) {

                wins++;

            } else if ("AI".equalsIgnoreCase(winner)) {

                losses++;

            } else if ("DRAW".equalsIgnoreCase(winner)) {

                draws++;
            }
        }

        Map<String, Integer> stats =
                new HashMap<>();

        stats.put(
                "wins",
                wins
        );

        stats.put(
                "losses",
                losses
        );

        stats.put(
                "draws",
                draws
        );

        stats.put(
                "total",
                debates.size()
        );

        return stats;
    }
}