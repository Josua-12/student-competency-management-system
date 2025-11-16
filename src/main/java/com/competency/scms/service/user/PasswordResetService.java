package com.competency.scms.service.user;

import com.competency.scms.domain.user.PhoneVerification;
import com.competency.scms.domain.user.User;
import com.competency.scms.domain.user.VerificationStatus;
import com.competency.scms.dto.auth.*;
import com.competency.scms.exception.BusinessException;
import com.competency.scms.exception.ErrorCode;
import com.competency.scms.repository.user.PhoneVerificationRepository;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.service.mail.EmailService;
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
    private final EmailService emailService;

    // 1) 사용자 확인
    public VerifyUserResponseDto verifyUser(VerifyUserRequestDto request) {
        int userNum = Integer.parseInt(request.getUserNum());
        User user = userRepository.findByUserNumAndName(userNum, request.getUserName())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 인증번호 생성 및 이메일 발송
        String code = generateCode(6);
        emailService.sendVerificationCode(user.getEmail(), code);

        // DB에 인증번호 저장 (이메일 기반으로)
        LocalDateTime now = LocalDateTime.now();
        PhoneVerification pv = PhoneVerification.builder()
                .user(user)
                .phone(user.getEmail()) // 이메일을 phone 필드에 저장
                .receiverEmail(user.getEmail())
                .verificationCode(code)
                .status(VerificationStatus.PENDING)
                .expiredAt(now.plusMinutes(10))
                .build();

        phoneVerificationRepository.save(pv);

        return VerifyUserResponseDto.builder()
                .success(true)
                .message("인증번호가 이메일로 발송되었습니다")
                .userNum(request.getUserNum())
                .userName(request.getUserName())
                .email(emailService.maskEmail(user.getEmail())) // 마스킹된 이메일 반환
                .realEmail(user.getEmail()) // 실제 이메일 반환
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
                .receiverEmail(user.getEmail())
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

    // 3) 인증번호 검증 - 수정된 부분
    public VerifyCodeResponseDto verifyCode(VerifyCodeRequestDto request) {
        // userNum으로 사용자 찾기
        int userNum = Integer.parseInt(request.getUserNum());
        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        // 디버깅 로그 추가
        log.info("=== 인증번호 검증 디버깅 ===");
        log.info("요청 userNum: {}", request.getUserNum());
        log.info("요청 인증번호: {}", request.getVerificationCode());
        log.info("사용자 이메일: {}", user.getEmail());

        // 실제 이메일로 조회
        PhoneVerification pv = phoneVerificationRepository
                .findTopByPhoneAndVerificationCodeAndStatusOrderByCreatedAtDesc(
                        user.getEmail(), // 실제 이메일 사용
                        request.getVerificationCode(),
                        VerificationStatus.PENDING)
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
                .findTopByPhoneAndStatusOrderByCreatedAtDesc(user.getEmail(), VerificationStatus.VERIFIED)
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
