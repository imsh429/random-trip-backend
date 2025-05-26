package com.randomtrip.backend.service;

import com.randomtrip.backend.dto.RandomTripResponse;
import com.randomtrip.backend.dto.TripPlanRequest;
import com.randomtrip.backend.dto.TripPlanResponse;
import com.randomtrip.backend.dto.TripSpot;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TripService {

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
                    continue; // skip header
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
                        System.err.println("⚠️ 숫자 변환 오류 발생 → 스킵됨: " + line);
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

    public TripPlanResponse getPlannedRoute(TripPlanRequest request) {
        String mood = request.getMood();
        String region = request.getRegion();

        // 🔥 실제 GPT 응답 대신 MOCK 데이터 사용 (위도/경도는 임시)
        // TODO : 나중에 gpt응답 입히기
        List<TripSpot> route = new ArrayList<>();

        if (region.equals("장성군") && mood.equals("healing")) {
            route.add(new TripSpot("장성호 수변길", 35.3083, 126.7871));
            route.add(new TripSpot("백양사", 35.4301, 126.8347));
            route.add(new TripSpot("장성 편백나무숲", 35.3123, 126.7689));
        } else {
            // 기본 응답
            route.add(new TripSpot("로컬 명소 1", 35.0, 126.7));
            route.add(new TripSpot("로컬 명소 2", 35.1, 126.8));
        }

        return new TripPlanResponse(route);
    }
}
