//경로 표시할 polyline 좌표
package com.randomtrip.backend.dto;

import com.randomtrip.backend.entity.Trip;
import com.randomtrip.backend.entity.TripRoute;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LatLngPoint {
    private double lat;
    private double lng;
    private int sectionId;
    private String roadName;
    private int distance;
    private int duration;

    public TripRoute toEntity(Trip trip, int sequence) {
        return TripRoute.builder()
                .trip(trip)
                .lat(lat)
                .lng(lng)
                .sequence(sequence)
                .sectionId(sectionId)
                .roadName(roadName)
                .distance(distance)
                .duration(duration)
                .build();
    }
}
