// (사용자의 http요청)api 요청 처리 (rest endpoint)
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
@RequestMapping("/oauth")
@RequiredArgsConstructor
// TODO : 배포된 프론트 주소로 바꾸기
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @GetMapping("callback/kakao")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        try {
            // 1. 사용자 정보 조회
            KakaoUserInfo userInfo = kakaoAuthService.getUserInfo(code);

            // 2. DB에서 유저 생성 or 조회
            User user = userService.findOrCreateUser(userInfo);

            // 3. JWT 발급
            String accessToken = jwtProvider.createAccessToken(user.getId());
            String refreshToken = jwtProvider.createRefreshToken(user.getId());

            // 4. JSON으로 성공 응답
            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken,
                    "nickname", user.getNickname()));

        } catch (Exception e) {
            // 5. 오류 발생 시 JSON 형태로 실패 응답
            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", "login_failed", "message", e.getMessage()));
        }
    }

}
