package com.randomtrip.backend.service;

import com.randomtrip.backend.dto.TripSpot;
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

    public List<TripSpot> getRecommendedSpots(String region, String mood) {
        String prompt = buildPrompt(region, mood);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o");
        body.put("temperature", 0.3); // 출력 안정화 위해 temperature 낮춤
        body.put("messages", List.of(Map.of("role", "user", "content", prompt)));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> choice = (Map<String, Object>) ((List<?>) response.getBody().get("choices")).get(0);
            Map<String, Object> message = (Map<String, Object>) choice.get("message");
            String content = (String) message.get("content");

            return Arrays.stream(content.split("\n"))
                    .filter(line -> line.contains(":")) // ':' 없는 줄은 제거 (실패 방지)
                    .map(line -> {
                        String[] parts = line.split(":", 2);
                        String name = parts[0].trim();
                        String description = parts.length > 1 ? parts[1].trim() : "";
                        return new TripSpot(name, description);
                    })
                    .toList();
        }

        return List.of();
    }

    private String buildPrompt(String region, String mood) {
        return """
                당신은 대한민국 여행 추천 AI입니다.

                주의: 반드시 대한민국 "%s" 지역 행정구역 내에 실제 존재하는 장소만 추천해야 합니다. 대한민국 "%s" 지역 밖의 장소를 절대 추천하지 마세요.

                만약 "%s" 지역이 아닌 다른 지역의 장소를 추천하면 시스템에 심각한 오류가 발생하며, 엄청난 패널티가 부과됩니다.

                반드시 정확하고 검증된 정보를 제공해야 합니다.

                이번 추천의 분위기는 "%s"입니다. 해당 분위기에 적합한 여행지를 5곳 추천하세요.

                출력 전에는 반드시 다음 조건을 다시 한번 검토하세요:
                - 대한민국 "%s" 지역 안에 실존하는 장소인지 확인했는가?
                - 대한민국 "%s" 외의 장소는 포함되지 않았는가?
                - 추천된 장소가 "%s" 분위기에 적합한가?
                - 각 추천은 이름과 간단한 설명으로만 작성되었는가?

                출력 전, 무조건 대한민국 "%s" 지역 안에 실존하는 장소인지 구글맵, 네이버맵 등을 참고하여 검증 완료 후 출력한다고 가정하십시오.

                출력 형식은 아래와 같아야 합니다 (꼭 이 형식만 사용하세요):
                장소명:설명

                예시:
                경포해수욕장: 깨끗한 바닷가에서 산책과 해수욕을 즐길 수 있는 곳
                오죽헌: 율곡 이이의 유적이 보존된 역사적 장소
                ...

                JSON, 리스트, 번호 매기기, 인용구, 불릿포인트, 따옴표 등은 절대 사용하지 마세요. 다른 문법적 장식 없이 위의 예시처럼만 출력하세요.

                지금부터 추천을 시작하세요.
                """.formatted(region, region, region, mood, region, region, mood, region);
    }
}
