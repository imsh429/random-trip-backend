package com.randomtrip.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GPTService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    public List<String> getRecommendedSpots(String region, String mood) {
        String prompt = region + "에서 " + mood + " 여행지 5곳을 추천해줘. 장소 이름만 한 줄에 하나씩 출력해줘.";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", List.of(
                Map.of("role", "user", "content", prompt)));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> choice = (Map<String, Object>) ((List<?>) response.getBody().get("choices")).get(0);
            Map<String, Object> message = (Map<String, Object>) choice.get("message");
            String content = (String) message.get("content");

            return Arrays.stream(content.split("\n"))
                    .map(line -> line.replaceAll("^[0-9.\\-\\s]+", "")) // 번호 제거
                    .filter(s -> !s.isBlank())
                    .toList();
        }

        return List.of("GPT 호출 실패");
    }
}
