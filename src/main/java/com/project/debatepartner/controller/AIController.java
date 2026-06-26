package com.project.debatepartner.controller;

import com.project.debatepartner.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/debate")
public class AIController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/reply")
    public String debate(
            @RequestParam String topic,
            @RequestParam String message
    ) {
        return geminiService.getDebateResponse(topic, message, "for");
    }
}