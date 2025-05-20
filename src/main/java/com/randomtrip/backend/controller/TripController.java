package com.randomtrip.backend.controller;

import com.randomtrip.backend.dto.RandomTripResponse;
import com.randomtrip.backend.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trip")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TripController {

    private final TripService tripService;

    @GetMapping("/random")
    public RandomTripResponse getRandomTrip() {
        return tripService.getRandomTrip();
    }
}
