package com.project.debatepartner.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String getDebateResponse(String topic, String userMessage, String userStance) {

        RestTemplate restTemplate = new RestTemplate();

        // Determine AI stance: If user is 'for' the motion, AI is 'against' it, and vice-versa.
        String aiStance = "for".equalsIgnoreCase(userStance) ? "against" : "for";

        // 🔥 STRONG, EASY-TO-UNDERSTAND DEBATE PROMPT
        String prompt = "You are an AI debate opponent participating in a friendly debate.\n"
                + "Topic: " + topic + "\n"
                + "Your Position: You are absolutely " + aiStance.toUpperCase() + " the topic.\n"
                + "User Argument: " + userMessage + "\n\n"
                + "Rules:\n"
                + "- You must take the " + aiStance.toUpperCase() + " side of the topic.\n"
                + "- Directly disagree with the user's argument.\n"
                + "- Provide logical, clear, and very easy-to-understand counter-arguments.\n"
                + "- Do NOT use complex jargon or formal vocabulary; use simple, everyday language.\n"
                + "- Keep it short (2-3 sentences).\n"
                + "- Be confident but polite.\n\n"
                + "Response:";

        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));

        // 🔥 Better variation
        Map<String, Object> config = new HashMap<>();
        config.put("temperature", 0.9);
        config.put("topP", 0.95);
        config.put("topK", 40);

        requestBody.put("generationConfig", config);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String url = apiUrl.trim() + "?key=" + apiKey.trim();

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            List candidates = (List) response.getBody().get("candidates");
            Map candidate = (Map) candidates.get(0);
            Map contentMap = (Map) candidate.get("content");
            List parts = (List) contentMap.get("parts");
            Map textPart = (Map) parts.get(0);

            return textPart.get("text").toString();

        } catch (Exception e) {
            System.err.println("Gemini API Error: " + e.getMessage());
            return "AI failed to respond. Please try again.";
        }
    }
}