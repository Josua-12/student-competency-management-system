package com.competency.scms.security;

import com.competency.scms.domain.user.User;
import com.competency.scms.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security가 사용자를 로드하는 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("사용자 정보 로드 시도 - username: '{}', 길이: {}", username, username.length());

        if (username == null || username.trim().isEmpty()) {
            log.warn("빈 username으로 로그인 시도");
            throw new UsernameNotFoundException("Empty username");
        }

        User user;

        // 이메일 형식인지 확인
        if (username.contains("@")) {
            log.info("이메일로 사용자 조회: {}", username);
            user = userRepository.findByEmail(username)
                    .orElseThrow(() -> {
                        log.warn("사용자를 찾을 수 없음 - 이메일: {}", username);
                        return new UsernameNotFoundException("User not found with email: " + username);
                    });
        } else {
            // 학번으로 조회 (기존 로직)
            try {
                Integer userNumber = Integer.parseInt(username.trim());
                log.info("파싱된 학번: {}", userNumber);

                user = userRepository.findByUserNum(userNumber)
                        .orElseThrow(() -> {
                            log.warn("사용자를 찾을 수 없음 - 학번: {}", userNumber);
                            return new UsernameNotFoundException("User not found with userNum: " + userNumber);
                        });
            } catch (NumberFormatException e) {
                log.warn("잘못된 username 형식: '{}'", username);
                throw new UsernameNotFoundException("Invalid username format: " + username);
            }
        }

        log.info("사용자 정보 로드 완료 - 학번: {}, 이름: {}", user.getUserNum(), user.getName());
        return new CustomUserDetails(user);
    }

}
