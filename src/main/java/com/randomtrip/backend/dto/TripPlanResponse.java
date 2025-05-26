package com.randomtrip.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class TripPlanResponse {
    private List<TripSpot> route;
}
