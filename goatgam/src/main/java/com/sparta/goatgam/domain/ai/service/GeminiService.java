package com.sparta.goatgam.domain.ai.service;

import com.sparta.goatgam.domain.owner.entity.Food;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${google.gemini.api-key}")
    private String apiKey;

    @Value("${google.gemini.model}")
    private String modelName;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateMenuDescription(Food food, String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;

        Map<String, Object> body = Map.of("contents", List.of(Map.of("parts", List.of(Map.of("text", food.getFoodName()+"의 메뉴 설명을 작성할거야. 50글자 내로 간략하게 " + prompt)))));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            e.printStackTrace();
            return "AI 설명 생성 실패";
        }
    }
}

