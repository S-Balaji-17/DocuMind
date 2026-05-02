package com.documind.documind.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class GeminiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_KEY = "YOUR_API_KEY";
    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;

    public String generateAnswer(String question, List<String> contextChunks) {
        try {
            if (contextChunks == null || contextChunks.isEmpty() || contextChunks.stream().allMatch(String::isBlank)) {
                return "FALLBACK: No relevant context found in the document for your question.";
            }

            StringBuilder context = new StringBuilder();
            for (String chunk : contextChunks) {
                context.append(chunk).append("\n\n");
            }

            String prompt = "You are a helpful document assistant. Answer the question based ONLY on the provided context. " +
                    "If the answer is not in the context, say 'The document does not contain information about this topic.'\n\n" +
                    "Context:\n" + context +
                    "\nQuestion: " + question +
                    "\n\nAnswer:";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> content = new HashMap<>();
            List<Map<String, Object>> parts = new ArrayList<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            parts.add(part);
            content.put("parts", parts);
            contents.add(content);
            body.put("contents", contents);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_URL, request, Map.class);

            List<Map> candidates = (List<Map>) response.getBody().get("candidates");
            Map firstCandidate = candidates.get(0);
            Map contentResponse = (Map) firstCandidate.get("content");
            List<Map> partsResponse = (List<Map>) contentResponse.get("parts");
            return (String) partsResponse.get(0).get("text");

        } catch (Exception e) {
            return "FALLBACK: Unable to generate answer. Error: " + e.getMessage();
        }
    }
}