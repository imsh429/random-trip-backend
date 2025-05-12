// 컨트롤러로부터 요청 받음 -> 실제 로직 수행
// (db조회, 외부 api호출, 데이터 가공) : service 역할
// 카카오 api에 요청 보냄 -> 사용자 정보 파싱
package com.randomtrip.backend.service;

import com.randomtrip.backend.dto.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.user-info-uri}")
    private String userInfoUri;

    private final RestTemplate restTemplate = new RestTemplate();

    public KakaoUserInfo getUserInfo(String code) {
        // 1. 액세스 토큰 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

        String accessToken = (String) response.getBody().get("access_token");

        // 2. 사용자 정보 요청
        HttpHeaders infoHeaders = new HttpHeaders();
        infoHeaders.setBearerAuth(accessToken);
        HttpEntity<?> infoRequest = new HttpEntity<>(infoHeaders);

        ResponseEntity<Map> userInfoRes = restTemplate.exchange(
                userInfoUri, HttpMethod.GET, infoRequest, Map.class);

        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfoRes.getBody().get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        KakaoUserInfo userInfo = new KakaoUserInfo();
        userInfo.setId(Long.parseLong(userInfoRes.getBody().get("id").toString()));
        userInfo.setNickname((String) profile.get("nickname"));

        return userInfo;
    }
}
