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

    public String getDebateResponse(String topic, String userMessage) {

        RestTemplate restTemplate = new RestTemplate();

        // 🔥 STRONG DEBATE PROMPT
        String prompt = "You are an AI debate opponent.\n"
                + "Topic: " + topic + "\n"
                + "User Argument: " + userMessage + "\n\n"
                + "Rules:\n"
                + "- Always disagree with the user\n"
                + "- Give logical, strong counter arguments\n"
                + "- No neutral answers\n"
                + "- Keep it short (2-4 lines)\n"
                + "- Be confident and persuasive\n\n"
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

        String url = apiUrl + "?key=" + apiKey;

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