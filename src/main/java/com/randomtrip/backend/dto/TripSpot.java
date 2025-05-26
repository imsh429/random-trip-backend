package com.randomtrip.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TripSpot {
    private String name;
    private double latitude;
    private double longitude;
}