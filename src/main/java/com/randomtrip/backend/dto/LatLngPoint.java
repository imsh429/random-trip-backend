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

    public TripRoute toEntity(Trip trip, int sequence) {
        return TripRoute.builder()
                .trip(trip)
                .lat(this.lat)
                .lng(this.lng)
                .sequence(sequence)
                .build();
    }
}
