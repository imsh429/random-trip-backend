// kakao 사용자 정보 dto
// dto : 외부로 데이터 주고받을 때 구조화 (추출)
package com.randomtrip.backend.dto;

import lombok.Data;

@Data
public class KakaoUserInfo {
    private Long id;
    private String nickname;
}
