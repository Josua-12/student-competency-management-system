package com.competency.SCMS.service.auth;

import com.competency.SCMS.domain.user.User;
import com.competency.SCMS.domain.user.PhoneVerification;
import com.competency.SCMS.domain.user.LoginHistory;
import com.competency.SCMS.dto.user.PhoneVerificationRequestDto;
import com.competency.SCMS.dto.user.PasswordResetNewRequestDto;
import com.competency.SCMS.dto.user.PhoneVerificationResponseDto;
import com.competency.SCMS.dto.auth.LoginRequestDto;
import com.competency.SCMS.dto.auth.LoginResponseDto;
import com.competency.SCMS.repository.user.LoginHistoryRepository;
import com.competency.SCMS.repository.user.UserRepository;
import com.competency.SCMS.repository.verification.PhoneVerificationRepo;
import com.competency.SCMS.exception.*;
import com.competency.SCMS.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final PhoneVerificationRepo phoneVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Value("${mail.receive.email}")
    private String receiverEmail;

    /**
     * 로그인 (AUTH-001)
     */
    public LoginResponseDto login(LoginRequestDto request, String ipAddress, String userAgent) {
        User user = userRepository.findByStudentNum(request.getStudentNum())
                .orElseThrow(() -> new UserNotFoundException("등록된 사용자가 없습니다."));

        if (user.getLocked()) {
            loginHistoryRepository.save(LoginHistory.builder()
                    .user(user)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .isSuccess(false)
                    .failReason("계정 잠금됨")
                    .build());
            throw new RuntimeException("계정이 잠금되었습니다. 관리자에게 문의하세요.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.addFailAttempt();
            userRepository.save(user);

            loginHistoryRepository.save(LoginHistory.builder()
                    .user(user)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .isSuccess(false)
                    .failReason("비밀번호 불일치")
                    .build());

            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        loginHistoryRepository.save(LoginHistory.builder()
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .isSuccess(true)
                .build());

        user.resetFailAttempt();
        userRepository.save(user);

        log.info("로그인 성공: userId={}, studentNum={}", user.getId(), user.getUserNum());

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .name(user.getName())
                .studentNum(user.getUserNum())
                .role(user.getRole().name())  // ✓ .name() 추가
                .message("로그인 성공")
                .build();
    }

    /**
     * 로그아웃 (AUTH-002)
     */
    public void logout(String token) {
        log.info("사용자 로그아웃 완료");
    }

    /**
     * Token 갱신 (AUTH-006)
     */
    public String refreshAccessToken(String refreshToken) {
        jwtUtil.validateToken(refreshToken);

        if (!"REFRESH".equals(jwtUtil.getTokenType(refreshToken))) {
            throw new JwtException("Refresh Token이 아닙니다.");
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        return jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
    }

    /**
     * 비밀번호 찾기 Step 1: 인증 코드 생성 및 SMS 링크 제공 (AUTH-003, AUTH-004)
     */
    public PhoneVerificationResponseDto requestPhoneAuthentication(PhoneVerificationRequestDto request) {
        LocalDate birthDate = convertBirthDateToLocalDate(request.getBirthDate());
        User user = userRepository.findByNameAndStudentNumAndBirthDate(
                request.getName(),
                request.getStudentNum(),
                birthDate
        ).orElseThrow(() -> new UserNotFoundException("사용자 정보가 일치하지 않습니다."));

        phoneVerificationRepository.findLatestUnverifiedByUserId(user.getId(), LocalDateTime.now())
                .ifPresent(pv -> {
                    if (!pv.isExpired()) {
                        throw new RuntimeException("이미 발급된 인증 코드가 있습니다. 잠시 후 다시 시도해주세요.");
                    }
                });

        String verificationCode = generateVerificationCode();

        PhoneVerification verification = PhoneVerification.builder()
                .user(user)
                .phone(formatPhoneNumber(request.getPhone()))
                .verificationCode(verificationCode)
                .receiverEmail(receiverEmail)
                .isVerified(false)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();
        phoneVerificationRepository.save(verification);

        String messageBody = user.getName() + " 인증번호: " + verificationCode;
        String smsLink = buildSmsLink(receiverEmail, messageBody);

        log.info("휴대폰 인증 요청: userId={}, phone={}", user.getId(), request.getPhone());

        return PhoneVerificationResponseDto.builder()
                .success(true)
                .message("인증 코드가 생성되었습니다. 아래 링크를 클릭하여 문자를 보내주세요.")
                .smsLink(smsLink)
                .verificationCode(verificationCode)
                .expiresIn(10 * 60L)
                .build();
    }

    /**
     * 비밀번호 찾기 Step 2: 인증 완료 확인 (프론트에서 주기적으로 폴링)
     */
    public boolean checkPhoneAuthStatus(String phone, String verificationCode) {
        PhoneVerification verification = phoneVerificationRepository
                .findByPhoneAndCodeAndNotExpired(
                        formatPhoneNumber(phone),
                        verificationCode,
                        LocalDateTime.now()
                )
                .orElseThrow(() -> new PhoneNotVerifiedException("인증 정보를 찾을 수 없습니다."));

        if (verification.isExpired()) {
            throw new PhoneExpiredException("인증 코드가 만료되었습니다.");
        }

        log.debug("인증 상태 확인: phone={}, verified={}", phone, verification.getIsVerified());

        return verification.getIsVerified();
    }

    /**
     * 비밀번호 찾기 Step 3: 비밀번호 재설정
     */
    public void resetPasswordWithPhoneAuth(PasswordResetNewRequestDto request) {
        PhoneVerification verification = phoneVerificationRepository
                .findByPhoneAndCodeAndNotExpired(
                        formatPhoneNumber(request.getPhone()),
                        request.getVerificationCode(),
                        LocalDateTime.now()
                )
                .orElseThrow(() -> new PhoneNotVerifiedException("유효한 인증 정보가 없습니다."));

        if (!verification.getIsVerified()) {
            throw new PhoneNotVerifiedException("휴대폰 인증이 아직 완료되지 않았습니다.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }

        validatePassword(request.getNewPassword());

        User user = verification.getUser();
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        verification.setIsVerified(false);
        phoneVerificationRepository.save(verification);

        log.info("비밀번호 재설정 완료: userId={}", user.getId());
    }

    private String buildSmsLink(String recipientEmail, String messageBody) {
        return UriComponentsBuilder
                .fromPath("sms:{recipient}")
                .queryParam("body", messageBody)
                .buildAndExpand(recipientEmail)
                .toUriString();
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(999999);
        return String.format("%06d", code);
    }

    private String formatPhoneNumber(String phone) {
        return phone.replaceAll("-", "");
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }

        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        if (!hasDigit || !hasSpecial) {
            throw new IllegalArgumentException("비밀번호는 숫자와 특수문자를 포함해야 합니다.");
        }
    }

    private LocalDate convertBirthDateToLocalDate(String birthDate) {
        try {
            int year = Integer.parseInt(birthDate.substring(0, 2));
            int month = Integer.parseInt(birthDate.substring(2, 4));
            int day = Integer.parseInt(birthDate.substring(4, 6));

            int fullYear = year < 30 ? 2000 + year : 1900 + year;

            return LocalDate.of(fullYear, month, day);
        } catch (Exception e) {
            throw new IllegalArgumentException("생년월일 형식이 잘못되었습니다. (YYMMDD)");
        }
    }
}
