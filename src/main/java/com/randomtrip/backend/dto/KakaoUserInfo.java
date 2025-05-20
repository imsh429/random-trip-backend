// kakao 사용자 정보 dto
// dto : 외부 데이터 전달용 구조체
package com.randomtrip.backend.dto;

import lombok.Data;

@Data
public class KakaoUserInfo {
    private Long id;
    private String nickname;
}
