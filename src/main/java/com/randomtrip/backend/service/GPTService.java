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

    public List<String> getPlaceAndAddressList(String region, String mood) {
        String prompt = buildPrompt(region, mood);

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
                    .filter(s -> s.contains(":")) // 유효한 포맷만
                    .toList();
        }

        return List.of("GPT 호출 실패");
        }

    private String buildPrompt(String region, String mood) {
        String moodExplanation = switch (mood.toLowerCase()) {
   

            case "healing" -> "조용히 쉬면서 자연을 구경할 수 있는 장소";
            case "adventure" -> "활동적이고 흥미로운 체험이 가능한 장소";
            case "romantic" -> "연인과 함께 분위기를 즐길 수 있는 장소";
            default -> "요즘 인기가 많은 명소";
        };

        return """
            당신은 대한민국 여행지 추천 AI입니다.

            다음 조건에 맞는 여행지를 5곳 추천해주세요.

            [조건]
            - 반드시 대한민국 %s 지역 내에 위치해야 합니다.
            - 각 장소는 반드시 **정확한 도로명 주소**를 포함해야 합니다.
            - 출력 형식은 [장소명]: [정확한 도로명 주소] 여야 합니다.
            - 도로명 주소는 네이버 지도에서 검색 가능한 형태여야 합니다.
            - 절대 번호, 설명, 여는 문장 없이 출력할 것
            - 각 장소는 한 줄에 하나만 출력해야 합니다.

            [출력 예시]
            경포해수욕장: 강원 강릉시 안현동 산1
            오죽헌: 강원 강릉시 율곡로3139번길 24
            강문해변: 강원 강릉시 창해로14번길 20
            주문진항: 강원 강릉시 주문진읍 주문로 33
            남항진해변: 강원 강릉시 해안로 367
            """.formatted(region, moodExplanation);
    }
}
