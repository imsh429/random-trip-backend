// 외부 요청 진입점 => 내부서비스(kakaoauthservice) 호출
// 로그인 요청 처리
package com.randomtrip.backend.controller;

import com.randomtrip.backend.dto.KakaoUserInfo;
import com.randomtrip.backend.entity.User;
import com.randomtrip.backend.service.KakaoAuthService;
import com.randomtrip.backend.service.UserService;
import com.randomtrip.backend.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoAuthService kakaoAuthService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        KakaoUserInfo userInfo = kakaoAuthService.getUserInfo(code);

        // 사용자 저장 or 조회
        User user = userService.findOrCreateUser(userInfo);

        // JWT 발급
        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "nickname", user.getNickname()));
    }
}
