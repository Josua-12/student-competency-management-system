package com.competency.scms.service.user;

import com.competency.scms.domain.user.LoginHistory;
import com.competency.scms.domain.user.User;
import com.competency.scms.dto.auth.LoginRequestDto;
import com.competency.scms.dto.auth.LoginResponseDto;
import com.competency.scms.exception.BusinessException;
import com.competency.scms.exception.ErrorCode;
import com.competency.scms.repository.user.LoginHistoryRepository;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인
     */
    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUserNum(request.getUserNum())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        // 계정 잠금 확인
        if (user.getLocked()) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.addFailAttempt();  // ✓ 수정됨
            userRepository.save(user);
            recordLoginFailure(user, "비밀번호 불일치");
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 로그인 성공 시 실패 횟수 초기화
        user.resetFailAttempt();  // ✓ 수정됨
        userRepository.save(user);

        // JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        String refreshToken = jwtUtil.generateRefreshToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        // 로그인 기록
        recordLoginSuccess(user);

        log.info("로그인 성공 - 사용자: {}", user.getEmail());

        // ✓ 실제 DTO 구조에 맞게 수정
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .userNum(user.getUserNum())
                .role(user.getRole().name())
                .message("로그인 성공")
                .build();
    }

    /**
     * 로그인 성공 기록
     */
    private void recordLoginSuccess(User user) {
        LoginHistory history = LoginHistory.builder()
                .user(user)
                .loginAt(LocalDateTime.now())
                .isSuccess(true)
                .build();
        loginHistoryRepository.save(history);
    }

    /**
     * 로그인 실패 기록
     */
    private void recordLoginFailure(User user, String reason) {
        LoginHistory history = LoginHistory.builder()
                .user(user)
                .loginAt(LocalDateTime.now())
                .isSuccess(false)
                .failReason(reason)
                .build();
        loginHistoryRepository.save(history);
    }
}
