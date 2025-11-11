package com.competency.SCMS.service.user;

import com.competency.SCMS.domain.user.PhoneVerification;
import com.competency.SCMS.domain.user.User;
import com.competency.SCMS.domain.user.VerificationStatus;
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

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PhoneVerificationRepo phoneVerificationRepo;

    /**
     * 비밀번호 찾기 시작
     */
    public PhoneVerificationResponseDto startPasswordReset(PhoneVerificationRequestDto request) {
        // 사용자 존재 여부 확인
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 임시 토큰 생성 및 저장
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken, 30);
        userRepository.save(user);

        log.info("비밀번호 재설정 시작 - 휴대폰: {}", request.getPhone());

        // 인증 코드 생성
        return PhoneVerificationResponseDto.builder()
                .success(true)
                .message("인증 코드가 생성되었습니다.")
                .expiresIn(10 * 60L)
                .build();
    }

    /**
     * 비밀번호 재설정 완료
     */
    public void confirmPasswordReset(PasswordResetNewRequestDto request) {
        // 비밀번호 일치 확인
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 사용자 조회
        PhoneVerification verification = phoneVerificationRepo
                .findByPhoneAndStatus(request.getPhone(), VerificationStatus.VERIFIED)
                .orElseThrow(() -> new BusinessException(ErrorCode.PHONE_NOT_VERIFIED));

        // 인증 완료 시간이 5분 이내인지 확인 (선택사항)
        if (verification.getVerifiedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new BusinessException(ErrorCode.VERIFICATION_EXPIRED);
        }

        User user = verification.getUser();
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 비밀번호 변경
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        user.clearPasswordResetToken();
        userRepository.save(user);

        // 사용한 인증 기록 만료 처리
        verification.setStatus(VerificationStatus.USED);
        phoneVerificationRepo.save(verification);

        log.info("비밀번호 재설정 완료 - 사용자 ID: {}", user.getId());
    }
}
