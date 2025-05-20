// trip api endpoint
package com.randomtrip.backend.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/trip")
public class TripController {

    private final List<Map<String, Object>> regionList = new ArrayList<>();

    // 서버 시작 시 CSV에서 지역 데이터 불러오기
    @PostConstruct
    public void initRegionList() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource("LOC_CODE_UTF8.csv").getInputStream(),
                        StandardCharsets.UTF_8))) {

            // 첫 줄은 헤더라서 건너뜀
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 4) {
                    try {
                        String name = tokens[2].trim();
                        double lat = Double.parseDouble(tokens[3].trim());
                        double lng = Double.parseDouble(tokens[4].trim());
                        Map<String, Object> region = new HashMap<>();
                        region.put("location", name);
                        region.put("lat", lat);
                        region.put("lng", lng);
                        regionList.add(region);
                    } catch (NumberFormatException e) {
                        // 좌표 파싱 실패한 경우는 건너뜀
                        continue;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("[지역 정보 로딩 실패] " + e.getMessage());
        }
    }

    // 랜덤 지역 하나 반환
    @GetMapping("/random")
    public Map<String, Object> getRandomRegion() {
        if (regionList.isEmpty()) {
            return Map.of("error", "지역 데이터가 없습니다.");
        }
        int index = new Random().nextInt(regionList.size());
        return regionList.get(index);
    }
}
