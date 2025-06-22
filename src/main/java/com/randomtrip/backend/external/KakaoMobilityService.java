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

        Map<String, Object> body = new HashMap<>();
        body.put("origin", originMap);
        body.put("destination", destinationMap);
        body.put("priority", "RECOMMEND");

        // 경유지가 2개 이상일 때만 waypoints 추가
        if (spots.size() > 1) {
            List<Map<String, Object>> waypoints = new ArrayList<>();
            for (int i = 0; i < spots.size() - 1; i++) {
                SpotDTO s = spots.get(i);
                Map<String, Object> point = new HashMap<>();
                point.put("name", s.getName());
                point.put("x", s.getLng());
                point.put("y", s.getLat());
                waypoints.add(point);
            }
            body.put("waypoints", waypoints);
        }

        return body;
    }

    private RouteResponse parseRouteResponse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode routes = root.path("routes");

            if (routes.isEmpty()) {
                throw new RuntimeException("경로 정보가 없습니다.");
            }

            List<LatLngPoint> polyline = new ArrayList<>();
            List<SectionInfo> sectionInfos = new ArrayList<>();

            JsonNode sections = routes.get(0).path("sections");

            for (int sectionIndex = 0; sectionIndex < sections.size(); sectionIndex++) {
                JsonNode section = sections.get(sectionIndex);
                int distance = section.path("distance").asInt();
                int duration = section.path("duration").asInt();

                List<String> roadNames = new ArrayList<>();
                JsonNode roads = section.path("roads");

                for (JsonNode road : roads) {
                    String roadName = road.path("name").asText();
                    if (!roadName.isEmpty()) {
                        roadNames.add(roadName);
                    }

                    JsonNode vertexes = road.path("vertexes");
                    for (int i = 0; i < vertexes.size(); i += 2) {
                        double lng = vertexes.get(i).asDouble();
                        double lat = vertexes.get(i + 1).asDouble();

                        LatLngPoint point = LatLngPoint.builder()
                                .lat(lat)
                                .lng(lng)
                                .sectionId(sectionIndex)
                                .roadName(roadName)
                                .distance(distance)
                                .duration(duration)
                                .build();

                        polyline.add(point);
                    }
                }

                sectionInfos.add(new SectionInfo(distance, duration, roadNames));
            }

            return new RouteResponse(polyline, sectionInfos);

        } catch (Exception e) {
            throw new RuntimeException("Kakao 경로 응답 파싱 실패", e);
        }
    }

}
