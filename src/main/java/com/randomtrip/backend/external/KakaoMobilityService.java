package com.randomtrip.backend.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.randomtrip.backend.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class KakaoMobilityService {

    @Value("${kakao.client-id}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RouteResponse getOptimizedRoute(TripRequest request) {
        String url = "https://apis-navi.kakaomobility.com/v1/waypoints/directions";

        // 1. Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        // 2. Body 구성
        Map<String, Object> body = buildRequestBody(request);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // 3. API 호출
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, String.class);

        // 4. 응답 파싱
        return parseRouteResponse(response.getBody());
    }

    private Map<String, Object> buildRequestBody(TripRequest request) {
        SpotDTO origin = request.getStart();
        List<SpotDTO> spots = request.getSpots();

        // 출발지
        Map<String, Object> originMap = new HashMap<>();
        originMap.put("x", origin.getLng());
        originMap.put("y", origin.getLat());

        // 도착지: 마지막 spot
        SpotDTO destination = spots.get(spots.size() - 1);
        Map<String, Object> destinationMap = new HashMap<>();
        destinationMap.put("x", destination.getLng());
        destinationMap.put("y", destination.getLat());

        // 중간 경유지: 마지막을 제외한 spot들
        List<Map<String, Object>> waypoints = new ArrayList<>();
        for (int i = 0; i < spots.size() - 1; i++) {
            SpotDTO s = spots.get(i);
            Map<String, Object> point = new HashMap<>();
            point.put("name", s.getName());
            point.put("x", s.getLng());
            point.put("y", s.getLat());
            waypoints.add(point);
        }

        // 요청 본문 구성
        Map<String, Object> body = new HashMap<>();
        body.put("origin", originMap);
        body.put("destination", destinationMap);
        body.put("waypoints", waypoints);
        body.put("priority", "RECOMMEND"); // 경로 우선순위 옵션

        return body;
    }

    private RouteResponse parseRouteResponse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode vertexes = root
                    .path("routes").get(0)
                    .path("sections").get(0)
                    .path("roads").get(0)
                    .path("vertexes");

            List<LatLngPoint> polyline = new ArrayList<>();
            for (int i = 0; i < vertexes.size(); i += 2) {
                double lng = vertexes.get(i).asDouble();
                double lat = vertexes.get(i + 1).asDouble();
                polyline.add(new LatLngPoint(lat, lng)); // lat, lng 순서
            }

            return new RouteResponse(polyline);

        } catch (Exception e) {
            throw new RuntimeException("Kakao 경로 응답 파싱 실패", e);
        }
    }
}
