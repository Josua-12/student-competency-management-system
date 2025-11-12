package com.competency.SCMS.controller;

import com.competency.SCMS.dto.auth.*;
import com.competency.SCMS.service.user.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class PasswordResetApiController {

    private final PasswordResetService passwordResetService;

    /**
     * 1단계: 사용자 확인 (학번 + 이름)
     */
    @PostMapping("/verify-user")
    public ResponseEntity<VerifyUserResponseDto> verifyUser(
            @Valid @RequestBody VerifyUserRequestDto request) {

        log.info("사용자 확인 요청: {}", request.getUserNum());

        VerifyUserResponseDto response = passwordResetService.verifyUser(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 2단계: 인증번호 발송 (SMS)
     */
    @PostMapping("/send-verification")
    public ResponseEntity<SendVerificationResponseDto> sendVerificationCode(
            @Valid @RequestBody SendVerificationRequestDto request) {

        log.info("인증번호 발송 요청: {} - {}", request.getUserNum(), request.getPhoneNumber());

        SendVerificationResponseDto response = passwordResetService.sendVerificationCode(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 3단계: 인증번호 확인
     */
    @PostMapping("/verify-code")
    public ResponseEntity<VerifyCodeResponseDto> verifyCode(
            @Valid @RequestBody VerifyCodeRequestDto request) {

        log.info("인증번호 확인 요청: {} - {}", request.getUserNum(), request.getVerificationCode());

        VerifyCodeResponseDto response = passwordResetService.verifyCode(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 4단계: 비밀번호 재설정
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponseDto> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDto request) {

        log.info("비밀번호 재설정 요청");

        ResetPasswordResponseDto response = passwordResetService.resetPassword(request);
        return ResponseEntity.ok(response);
    }


}
