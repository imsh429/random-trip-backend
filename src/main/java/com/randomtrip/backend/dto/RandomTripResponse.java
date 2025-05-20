package com.randomtrip.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RandomTripResponse {
    private String korName;
    private String engName;
    private double latitude;
    private double longitude;
}
