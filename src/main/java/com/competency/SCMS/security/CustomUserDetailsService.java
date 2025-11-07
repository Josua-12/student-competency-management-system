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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("사용자 정보 로드 시도 - 이메일: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("사용자를 찾을 수 없음 - 이메일: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        log.info("사용자 정보 로드 완료 - 이메일: {}", email);
        return new CustomUserDetails(user);
    }
}
