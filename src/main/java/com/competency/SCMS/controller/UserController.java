package com.competency.SCMS.controller;

import com.competency.SCMS.dto.auth.LoginRequestDto;
import com.competency.SCMS.dto.auth.LoginResponseDto;
import com.competency.SCMS.exception.BusinessException;
import com.competency.SCMS.exception.ErrorCode;
import com.competency.SCMS.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.competency.SCMS.dto.user.*;
import com.competency.SCMS.service.auth.AuthService;
import com.competency.SCMS.service.user.PasswordResetService;
import com.competency.SCMS.service.user.PhoneVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final AuthService authenticationService;
    private final PhoneVerificationService phoneVerificationService;
    private final PasswordResetService passwordResetService;
    private final JwtUtil jwtUtil;

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        log.info("로그인 요청 - 이메일: {}", request.getEmail());

        // ✓ 수정: HttpServletRequest에서 IP와 User-Agent 추출
        HttpServletRequest httpRequest = getHttpServletRequest();
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        LoginResponseDto response = authenticationService.login(request, ipAddress, userAgent);
        return ResponseEntity.ok(response);
    }

    /**
     * 본인 인증 시작 (SMS 링크 생성)
     */
    @PostMapping("/phone/verify/start")
    public ResponseEntity<PhoneVerificationResponseDto> startPhoneVerification(
            @Valid @RequestBody PhoneVerificationRequestDto request) {
        log.info("본인 인증 시작 - 휴대폰: {}", request.getPhone()); // ✓ phoneNumber → phone
        PhoneVerificationResponseDto response = authenticationService.requestPhoneAuthentication(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 본인 인증 확인
     */
    @PostMapping("/phone/verify/confirm")
    public ResponseEntity<Map<String, Object>> confirmPhoneVerification(
            @RequestParam String phone,
            @RequestParam String verificationCode) {
        log.info("본인 인증 확인 - 휴대폰: {}", phone);
        boolean verified = authenticationService.checkPhoneAuthStatus(phone, verificationCode);
        return ResponseEntity.ok(Map.of(
                "verified", verified,
                "message", "본인 인증이 완료되었습니다."
        ));
    }

    /**
     * 비밀번호 찾기 (본인인증 포함)
     */
    @PostMapping("/password-reset/start")
    public ResponseEntity<PhoneVerificationResponseDto> startPasswordReset(
            @Valid @RequestBody PhoneVerificationRequestDto request) {
        log.info("비밀번호 찾기 시작 - 휴대폰: {}", request.getPhone()); // ✓ phoneNumber → phone
        PhoneVerificationResponseDto response = authenticationService.requestPhoneAuthentication(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 비밀번호 재설정 완료
     */
    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Map<String, String>> confirmPasswordReset(
            @Valid @RequestBody PasswordResetNewRequestDto request) {
        log.info("비밀번호 재설정 요청 - 휴대폰: {}", request.getPhone());
        authenticationService.resetPasswordWithPhoneAuth(request);
        return ResponseEntity.ok(Map.of(
                "message", "비밀번호가 성공적으로 변경되었습니다."
        ));
    }

    /**
     * HttpServletRequest 가져오기
     */
    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            return attrs.getRequest();
        }
        return null;
    }

    /**
     * 클라이언트 IP 주소 추출
     */
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) return "UNKNOWN";

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // JwtUtil로 리프레시 토큰 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 새 액세스 토큰 발급
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String email = jwtUtil.getEmailFromToken(refreshToken);
        String role = jwtUtil.getRoleFromToken(refreshToken);

        String newAccessToken = jwtUtil.generateAccessToken(userId, email, role);

        return ResponseEntity.ok(LoginResponseDto.builder()
                .accessToken(newAccessToken)
                .build());
    }

}
