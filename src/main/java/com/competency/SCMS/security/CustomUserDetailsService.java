package com.competency.SCMS.security;

import com.competency.SCMS.domain.user.User;
import com.competency.SCMS.repository.user.UserRepository;
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
    public UserDetails loadUserByUsername(String userNum) throws UsernameNotFoundException {
        log.info("사용자 정보 로드 시도 - 학번: '{}', 길이: {}", userNum, userNum.length());

        if (userNum == null || userNum.trim().isEmpty()) {
            log.warn("빈 학번으로 로그인 시도");
            throw new UsernameNotFoundException("Empty userNum");
        }

        try {
            Integer userNumber = Integer.parseInt(userNum.trim());
            log.info("파싱된 학번: {}", userNumber);
            
            User user = userRepository.findByUserNum(userNumber)
                    .orElseThrow(() -> {
                        log.warn("사용자를 찾을 수 없음 - 학번: {}", userNumber);
                        return new UsernameNotFoundException("User not found with userNum: " + userNumber);
                    });

            log.info("사용자 정보 로드 완료 - 학번: {}, 이름: {}", userNumber, user.getName());
            return new CustomUserDetails(user);
        } catch (NumberFormatException e) {
            log.warn("잘못된 학번 형식: '{}'", userNum);
            throw new UsernameNotFoundException("Invalid userNum format: " + userNum);
        }
    }
}
