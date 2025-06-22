package com.randomtrip.backend.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionInfo {
    private int distance; // meters
    private int duration; // seconds
    private List<String> roadNames; // 도로 이름들

}
