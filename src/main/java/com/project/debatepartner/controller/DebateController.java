package com.project.debatepartner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.project.debatepartner.model.Debate;
import com.project.debatepartner.repository.DebateRepository;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.project.debatepartner.service.GeminiService;

@RestController
public class DebateController {

    @Autowired
    private DebateRepository debateRepo;

    @Autowired
    private GeminiService geminiService;

    // =========================
    // ✅ AI DEBATE
    // =========================
    @PostMapping("/debate")
    public String handleDebate(@RequestBody Map<String, String> request) {

        String userArg = request.get("argument");
        String topic = request.get("topic");
        String stance = request.getOrDefault("stance", "for");

        return geminiService.getDebateResponse(topic, userArg, stance);
    }

    // =========================
    // ✅ SAVE DEBATE + WINNER LOGIC
    // =========================
    @PostMapping("/saveDebate")
    public String saveDebate(
            @RequestParam String username,
            @RequestParam String topic,
            @RequestParam String userArgument,
            @RequestParam String aiArgument
    ) {
        Debate debate = new Debate();

        debate.setUsername(username);
        debate.setTopic(topic);
        debate.setUserArgument(userArgument);
        debate.setAiArgument(aiArgument);

        // 🔥 WINNER LOGIC (simple)
        if(userArgument.length() > aiArgument.length()){
            debate.setWinner("User");
        } else {
            debate.setWinner("AI");
        }

        debate.setResult("Completed");

        debateRepo.save(debate);

        return "Saved";
    }

    // =========================
    // ✅ HISTORY
    // =========================
    @GetMapping("/history")
    public List<Debate> getHistory(@RequestParam String username) {
        return debateRepo.findByUsername(username);
    }

    // =========================
    // 🔥 STATS API
    // =========================
    @GetMapping("/stats")
    public Map<String, Integer> getStats(@RequestParam String username) {

        List<Debate> debates = debateRepo.findByUsername(username);

        int wins = 0;
        int losses = 0;

        for(Debate d : debates){
            if("User".equals(d.getWinner())){
                wins++;
            } else {
                losses++;
            }
        }

        Map<String, Integer> stats = new HashMap<>();
        stats.put("wins", wins);
        stats.put("losses", losses);
        stats.put("total", debates.size());

        return stats;
    }
}