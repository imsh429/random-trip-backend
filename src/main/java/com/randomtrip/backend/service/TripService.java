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

    private final GPTService gptService;

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

        // GPT로부터 추천 장소 이름 리스트를 받아옴
        List<String> spotNames = gptService.getPlaceAndAddressList(region, mood);

        // TODO : 위경도는 임시값 (나중에 kakao maps api이용해 대체)
        List<TripSpot> route = new ArrayList<>();
        double baseLat = 35.3;
        double baseLng = 126.8;

        for (int i = 0; i < spotNames.size(); i++) {
            String name = spotNames.get(i);
            double lat = baseLat + i * 0.01;
            double lng = baseLng + i * 0.01;
            route.add(new TripSpot(name, lat, lng));
        }

        return new TripPlanResponse(route);
    }
}
