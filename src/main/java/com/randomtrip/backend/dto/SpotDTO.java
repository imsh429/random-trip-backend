// 하나의 여행지 정보 담음
package com.randomtrip.backend.dto;

import lombok.*;

@Data
public class SpotDTO {
    private String name;
    private double lat;
    private double lng;
}
