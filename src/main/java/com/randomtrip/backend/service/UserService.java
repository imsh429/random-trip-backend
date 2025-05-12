// 사용자 저장.조회
package com.randomtrip.backend.service;

import com.randomtrip.backend.dto.KakaoUserInfo;
import com.randomtrip.backend.entity.User;
import com.randomtrip.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findOrCreateUser(KakaoUserInfo kakaoUserInfo) {
        return userRepository.findByKakaoId(kakaoUserInfo.getId())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .kakaoId(kakaoUserInfo.getId())
                            .nickname(kakaoUserInfo.getNickname())
                            .build();
                    return userRepository.save(newUser);
                });
    }
}
