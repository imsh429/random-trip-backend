package com.randomtrip.backend.dto;

import lombok.*;

import java.util.List;

@Data
public class TripRequest {
    private SpotDTO start;
    private List<SpotDTO> spots;
}
