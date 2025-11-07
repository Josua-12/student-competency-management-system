package com.competency.SCMS.controller;

import com.competency.SCMS.dto.user.*;
import com.competency.SCMS.service.user.AuthenticationService;
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

    private final AuthenticationService authenticationService;
    private final PhoneVerificationService phoneVerificationService;
    private final PasswordResetService passwordResetService;

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        log.info("로그인 요청 - 이메일: {}", request.getEmail());
        LoginResponseDto response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 본인 인증 시작 (SMS 링크 생성)
     */
    @PostMapping("/phone/verify/start")
    public ResponseEntity<PhoneVerificationResponseDto> startPhoneVerification(
            @Valid @RequestBody PhoneVerificationRequestDto request) {
        log.info("본인 인증 시작 - 휴대폰: {}", request.getPhoneNumber());
        PhoneVerificationResponseDto response = phoneVerificationService.startVerification(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 본인 인증 확인
     */
    @PostMapping("/phone/verify/confirm")
    public ResponseEntity<Map<String, Object>> confirmPhoneVerification(
            @Valid @RequestBody PhoneVerificationConfirmDto request) {
        log.info("본인 인증 확인 - 휴대폰: {}", request.getPhoneNumber());
        boolean verified = phoneVerificationService.verifyCode(request);
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
            @Valid @RequestBody PasswordResetStartDto request) {
        log.info("비밀번호 찾기 시작 - 이메일: {}", request.getEmail());
        PhoneVerificationResponseDto response = passwordResetService.startPasswordReset(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 비밀번호 재설정 완료
     */
    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Map<String, String>> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirmDto request) {
        log.info("비밀번호 재설정 요청 - 휴대폰: {}", request.getPhoneNumber());
        passwordResetService.confirmPasswordReset(request);
        return ResponseEntity.ok(Map.of(
                "message", "비밀번호가 성공적으로 변경되었습니다."
        ));
    }
}
