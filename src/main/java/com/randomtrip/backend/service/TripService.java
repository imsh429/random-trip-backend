package com.randomtrip.backend.service;

import com.randomtrip.backend.dto.*;
import com.randomtrip.backend.entity.*;
import com.randomtrip.backend.external.KakaoMobilityService;
import com.randomtrip.backend.repository.ConfirmedPlaceRepository;
import com.randomtrip.backend.repository.TripRepository;
import com.randomtrip.backend.repository.TripRouteRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final ConfirmedPlaceRepository placeRepository;
    private final KakaoMobilityService kakaoMobilityService;
    private final GPTService gptService;
    private final TripRouteRepository routeRepository;

    // 무작위 여행지 추천
    public RandomTripResponse getRandomTrip() {
        List<RandomTripResponse> trips = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource("LOC_CODE_UTF8.csv").getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean skipFirst = true;
            while ((line = reader.readLine()) != null) {
                if (skipFirst) {
                    skipFirst = false;
                    continue;
                }

                String[] tokens = line.split(",");
                if (tokens.length >= 5) {
                    try {
                        String engName = tokens[1].trim();
                        String korName = tokens[2].trim();
                        double latitude = Double.parseDouble(tokens[3].trim());
                        double longitude = Double.parseDouble(tokens[4].trim());

                        trips.add(new RandomTripResponse(korName, engName, latitude, longitude));
                    } catch (NumberFormatException e) {
                        System.err.println("⚠️ 숫자 변환 오류 → 스킵됨: " + line);
                    }
                } else {
                    System.err.println("⚠️ CSV 필드 수 부족 → 스킵됨: " + line);
                }
            }

            if (trips.isEmpty()) {
                throw new RuntimeException("유효한 여행지 데이터가 없습니다.");
            }

            Random random = new Random();
            return trips.get(random.nextInt(trips.size()));

        } catch (Exception e) {
            throw new RuntimeException("CSV 파일 로딩 실패: " + e.getMessage());
        }
    }

    // GPT 기반 추천 경로
    public TripPlanResponse getPlannedRoute(TripPlanRequest request) {
        List<TripSpot> route = gptService.getRecommendedSpots(request.getRegion(), request.getMood());
        return new TripPlanResponse(route);
    }

    public RouteResponse handleTripConfirmation(TripRequest request, Long userId) {
        // 1. Kakao Mobility API 호출
        RouteResponse route = kakaoMobilityService.getOptimizedRoute(request);

        // 2. Trip 저장
        Trip trip = tripRepository.save(Trip.builder()
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build());

        // 3. 출발지 제외한 장소 저장
        List<SpotDTO> spots = request.getSpots();
        for (SpotDTO spot : spots) {
            ConfirmedPlace place = ConfirmedPlace.builder()
                    .trip(trip)
                    .name(spot.getName())
                    .lat(spot.getLat())
                    .lng(spot.getLng())
                    .build();
            placeRepository.save(place);
        }

        List<LatLngPoint> polyline = route.getPolyline();
        // 4. TripRoute 저장 (polyline)
        for (int i = 0; i < polyline.size(); i++) {
            TripRoute routePoint = polyline.get(i).toEntity(trip, i);
            routeRepository.save(routePoint);
        }
        // 5. 프론트에 polyline 응답
        return route;
    }

    private final ConfirmedPlaceRepository confirmedPlaceRepository;

    public List<Map<String, Object>> getRecentTrips(Long userId) {
        List<ConfirmedPlace> places = confirmedPlaceRepository.findRecentByUserId(userId, PageRequest.of(0, 5));

        List<Map<String, Object>> result = new ArrayList<>();

        for (ConfirmedPlace place : places) {
            Map<String, Object> placeMap = new HashMap<>();
            placeMap.put("id", place.getId());
            placeMap.put("name", place.getName());

            String formattedDate = place.getTrip().getCreatedAt()
                    .toLocalDate()
                    .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            placeMap.put("date", formattedDate);

            result.add(placeMap);
        }

        return result;
    }

}
