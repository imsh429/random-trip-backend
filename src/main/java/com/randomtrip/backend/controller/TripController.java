package com.randomtrip.backend.controller;

import com.randomtrip.backend.dto.*;
import com.randomtrip.backend.service.TripService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.randomtrip.backend.entity.User;

@RestController
@RequestMapping("/trip")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // TODO: 배포 주소로 교체
public class TripController {

    private final TripService tripService;

    @GetMapping("/random")
    public RandomTripResponse getRandomTrip() {
        return tripService.getRandomTrip();
    }

    @PostMapping("/plan")
    public ResponseEntity<TripPlanResponse> planTrip(@RequestBody TripPlanRequest request) {
        TripPlanResponse result = tripService.getPlannedRoute(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/confirm")
    public ResponseEntity<RouteResponse> confirmTrip(
            @RequestBody TripRequest request,
            @AuthenticationPrincipal User user) {

        Long userId = user.getId(); // JWT에서 추출된 인증된 사용자 ID
        RouteResponse routeResponse = tripService.handleTripConfirmation(request, userId);
        return ResponseEntity.ok(routeResponse);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyRecentTrips(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<Map<String, Object>> recentTrips = tripService.getRecentTrips(user.getId());
        return ResponseEntity.ok(Map.of("spots", recentTrips));
    }
}
