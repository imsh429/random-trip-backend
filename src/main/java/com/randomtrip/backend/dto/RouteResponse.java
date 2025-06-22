package com.randomtrip.backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponse {
    private List<LatLngPoint> polyline;
    private List<SectionInfo> sections;
}