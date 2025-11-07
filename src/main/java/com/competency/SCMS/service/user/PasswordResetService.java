package com.competency.SCMS.service.user;

import com.competency.SCMS.domain.user.User;
import com.competency.SCMS.dto.user.*;
import com.competency.SCMS.exception.BusinessException;
import com.competency.SCMS.exception.ErrorCode;
import com.competency.SCMS.repository.user.UserRepository;
import com.competency.SCMS.repository.verification.PhoneVerificationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PhoneVerificationRepo phoneVerificationRepo;
    private final PasswordEncoder passwordEncoder;
    private final PhoneVerificationService phoneVerificationService;

    /**
     * 비밀번호 찾기 시작
     */
    public PhoneVerificationResponseDto startPasswordReset(PasswordResetStartDto request) {
        // 사용자 존재 여부 확인
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 휴대폰 인증 시작
        PhoneVerificationRequestDto verificationRequest = PhoneVerificationRequestDto.builder()
                .phoneNumber(request.getPhoneNumber())
                .purpose("PASSWORD_RESET")
                .build();

        PhoneVerificationResponseDto response = phoneVerificationService.startVerification(verificationRequest);

        // 임시 토큰 생성 및 저장
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken, 30);
        userRepository.save(user);

        log.info("비밀번호 재설정 시작 - 이메일: {}", request.getEmail());
        return response;
    }

    /**
     * 비밀번호 재설정 완료
     */
    public void confirmPasswordReset(PasswordResetConfirmDto request) {
        // 비밀번호 일치 확인
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 본인 인증 확인
        PhoneVerificationConfirmDto confirmDto = PhoneVerificationConfirmDto.builder()
                .phoneNumber(request.getPhoneNumber())
                .verificationCode(request.getVerificationCode())
                .build();

        boolean verified = phoneVerificationService.verifyCode(confirmDto);
        if (!verified) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_MISMATCH);
        }

        // 사용자 조회 및 비밀번호 변경
        User user = userRepository.findByPhone(request.getPhoneNumber())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        user.clearPasswordResetToken();
        userRepository.save(user);

        log.info("비밀번호 재설정 완료 - 사용자 ID: {}", user.getId());
    }
}
