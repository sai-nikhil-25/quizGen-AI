package com.kunduri.quizgen.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kunduri.quizgen.dto.Question;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AIService {

    private final String apiKey;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AIService(@Value("${gemini.api.key:}") String apiKey,
                     RestTemplate restTemplate,
                     ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;

        System.out.println("AIService initialized successfully");
    }

    public List<Question> generateQuestions(String topic, String level) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("Gemini API key is not configured");
        }

        String prompt = buildPrompt(topic, level);

        try {
            String aiResponse = callGemini(prompt);
            return parseQuestions(aiResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate questions: " + e.getMessage());
        }
    }

    private String buildPrompt(String topic, String level) {
        return String.format(
                "Generate exactly 10 conceptual interview based multiple choice questions about %s at %s level. " +
                        "For each question, provide exactly 4 options and indicate the correct answer. " +
                        "Return ONLY valid JSON in this exact format (no markdown, no extra text):\n" +
                        "{\n" +
                        "  \"questions\": [\n" +
                        "    {\n" +
                        "      \"question\": \"Question text?\",\n" +
                        "      \"options\": [\"Option A\", \"Option B\", \"Option C\", \"Option D\"],\n" +
                        "      \"correctAnswer\": 0\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}\n" +
                        "The correctAnswer should be the index (0-3) of the correct option. Return ONLY the JSON, nothing else.",
                topic, level
        );
    }

    private String callGemini(String prompt) {
        // Using gemini-2.5-flash - the latest stable model
        String url = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(
                Map.of("parts", List.of(Map.of("text", prompt)))
        ));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 1.0);
        generationConfig.put("topK", 64);
        generationConfig.put("topP", 0.95);
        generationConfig.put("maxOutputTokens", 8192);
        requestBody.put("generationConfig", generationConfig);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());

            JsonNode candidates = root.path("candidates");
            if (candidates.isEmpty()) {
                throw new RuntimeException("No response from Gemini");
            }

            String text = candidates.get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            return text;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to call Gemini API: " + e.getMessage(), e);
        }
    }

    private List<Question> parseQuestions(String jsonResponse) {
        try {
            jsonResponse = jsonResponse.trim();

            if (jsonResponse.startsWith("```json")) {
                jsonResponse = jsonResponse.substring(7);
            }
            if (jsonResponse.startsWith("```")) {
                jsonResponse = jsonResponse.substring(3);
            }
            if (jsonResponse.endsWith("```")) {
                jsonResponse = jsonResponse.substring(0, jsonResponse.length() - 3);
            }
            jsonResponse = jsonResponse.trim();

            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode questionsNode = root.path("questions");

            if (questionsNode.isMissingNode() || questionsNode.isEmpty()) {
                throw new RuntimeException("No questions found in response");
            }

            List<Question> questions = new ArrayList<>();
            for (JsonNode node : questionsNode) {
                Question question = new Question();
                question.setQuestion(node.path("question").asText());

                List<String> options = new ArrayList<>();
                for (JsonNode option : node.path("options")) {
                    options.add(option.asText());
                }
                question.setOptions(options);
                question.setCorrectAnswer(node.path("correctAnswer").asInt());

                questions.add(question);
            }

            if (questions.size() != 10) {
                throw new RuntimeException("Expected 10 questions but got " + questions.size());
            }

            return questions;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse questions: " + e.getMessage() + "\nResponse: " + jsonResponse, e);
        }
    }
}