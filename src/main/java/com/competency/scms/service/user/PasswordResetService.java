package com.competency.scms.service.user;

import com.competency.scms.domain.user.PhoneVerification;
import com.competency.scms.domain.user.User;
import com.competency.scms.domain.user.VerificationStatus;
import com.competency.scms.dto.auth.*;
import com.competency.scms.exception.BusinessException;
import com.competency.scms.exception.ErrorCode;
import com.competency.scms.repository.user.PhoneVerificationRepository;
import com.competency.scms.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PhoneVerificationRepository phoneVerificationRepository;

    // 1) 사용자 확인
    public VerifyUserResponseDto verifyUser(VerifyUserRequestDto request) {
        int userNum = Integer.parseInt(request.getUserNum());
        userRepository.findByUserNumAndName(userNum, request.getUserName())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return VerifyUserResponseDto.builder()
                .success(true)
                .message("사용자 확인 완료")
                .userNum(request.getUserNum())
                .userName(request.getUserName())
                .build();
    }

    // 2) 인증번호 생성/저장
    public SendVerificationResponseDto sendVerificationCode(SendVerificationRequestDto request) {
        int userNum = Integer.parseInt(request.getUserNum());
        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (request.getPhoneNumber() != null && user.getPhone() != null
                && !user.getPhone().equals(request.getPhoneNumber())) {
            throw new BusinessException(ErrorCode.PHONE_NUMBER_MISMATCH);
        }

        String code = generateCode(6);
        LocalDateTime now = LocalDateTime.now();

        PhoneVerification pv = PhoneVerification.builder()
                .user(user)
                .phone(user.getPhone() != null ? user.getPhone() : request.getPhoneNumber())
                .verificationCode(code)
                .status(VerificationStatus.PENDING)
                .expiredAt(now.plusMinutes(10))
                .build();

        phoneVerificationRepository.save(pv);
        log.info("인증번호 생성 - userNum: {}, phone: {}", request.getUserNum(), pv.getPhone());

        return SendVerificationResponseDto.builder()
                .success(true)
                .message("인증 코드가 생성되었습니다.")
                .expiresIn(10 * 60)
                .build();
    }

    // 3) 인증번호 검증
    public VerifyCodeResponseDto verifyCode(VerifyCodeRequestDto request) {
        PhoneVerification pv = phoneVerificationRepository
                .findTopByPhoneAndVerificationCodeAndStatusOrderByCreatedAtDesc(
                        request.getPhoneNumber(), request.getVerificationCode(), VerificationStatus.PENDING)
                .orElseThrow(() -> new BusinessException(ErrorCode.VERIFICATION_CODE_MISMATCH));

        if (pv.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.VERIFICATION_EXPIRED);
        }

        pv.setStatus(VerificationStatus.VERIFIED);
        phoneVerificationRepository.save(pv);

        String token = request.getUserNum() + ":" + pv.getVerificationCode();

        return VerifyCodeResponseDto.builder()
                .success(true)
                .message("인증이 완료되었습니다.")
                .token(token)
                .build();
    }

    // 4) 비밀번호 재설정
    public ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto request) {
        String[] parts = request.getToken().split(":");
        if (parts.length != 2) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        int userNum;
        try {
            userNum = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        String code = parts[1];

        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        PhoneVerification lastVerified = phoneVerificationRepository
                .findTopByPhoneAndStatusOrderByCreatedAtDesc(user.getPhone(), VerificationStatus.VERIFIED)
                .orElseThrow(() -> new BusinessException(ErrorCode.PHONE_NOT_VERIFIED));

        if (lastVerified.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.VERIFICATION_EXPIRED);
        }
        if (!code.equals(lastVerified.getVerificationCode())) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_MISMATCH);
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        lastVerified.setStatus(VerificationStatus.USED);
        phoneVerificationRepository.save(lastVerified);

        return ResetPasswordResponseDto.builder()
                .success(true)
                .message("비밀번호가 재설정되었습니다.")
                .build();
    }

    private String generateCode(int len) {
        var r = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(r.nextInt(10));
        return sb.toString();
    }
}
