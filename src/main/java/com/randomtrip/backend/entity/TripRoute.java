package com.randomtrip.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Trip trip;

    private double lat;
    private double lng;

    private int sequence; // 경로 순서를 보장하기 위한 인덱스

    private int sectionId;
    private String roadName;
    private int distance; // meters
    private int duration; // seconds

}
