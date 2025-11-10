package com.competency.SCMS.service.user;

import com.competency.SCMS.domain.user.PhoneVerification;
import com.competency.SCMS.domain.user.VerificationStatus;
import com.competency.SCMS.dto.user.*;
import com.competency.SCMS.exception.*;
import com.competency.SCMS.repository.verification.PhoneVerificationRepo;
import com.competency.SCMS.service.mail.MailReceiveService;
import com.competency.SCMS.util.PhoneVerificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PhoneVerificationService {

    private final PhoneVerificationRepo phoneVerificationRepo;
    private final PhoneVerificationUtil phoneVerificationUtil;
    private final MailReceiveService mailReceiveService;

    private static final int VERIFICATION_EXPIRY_MINUTES = 5;

    /**
     * 인증 코드 생성 및 SMS 링크 제공
     */
    public PhoneVerificationResponseDto startVerification(PhoneVerificationRequestDto request) {
        String normalizedPhone = phoneVerificationUtil.normalizePhoneNumber(request.getPhone());

        // 기존 PENDING 상태 인증 무효화
        phoneVerificationRepo.findByPhoneAndStatus(normalizedPhone, VerificationStatus.PENDING)
                .ifPresent(existing -> {
                    existing.markAsExpired();
                    phoneVerificationRepo.save(existing);
                });

        // 새 인증 코드 생성
        String verificationCode = phoneVerificationUtil.generateVerificationCode();
        String smsLink = phoneVerificationUtil.generateSmsLink(verificationCode);
        String receiverEmail = phoneVerificationUtil.getVerificationEmail();

        // DB에 저장
        PhoneVerification verification = PhoneVerification.builder()
                .phone(normalizedPhone)
                .verificationCode(verificationCode)
                .receiverEmail(receiverEmail)
                .status(VerificationStatus.PENDING)
                .expiredAt(LocalDateTime.now().plusMinutes(VERIFICATION_EXPIRY_MINUTES))
                .failureCount(0)
                .purpose(request.getPurpose())
                .build();

        phoneVerificationRepo.save(verification);

        mailReceiveService.startPolling(normalizedPhone);

        log.info("인증 코드 생성 완료 - 휴대폰: {}, 코드: {}", normalizedPhone, verificationCode);

        return PhoneVerificationResponseDto.builder()
                .success(true)  // ✓ 추가
                .verificationCode(verificationCode)
                .smsLink(smsLink)
                .phoneNumber(normalizedPhone)
                .receiverEmail(receiverEmail)
                .expiresAt(verification.getExpiredAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .message("SMS 앱에서 인증 메시지를 전송해주세요. 5분 내에 인증을 완료해야 합니다.")
                .build();
    }

    /**
     * 사용자가 보낸 메시지 검증
     */
    public boolean verifyCode(PhoneVerificationConfirmDto confirmDto) {
        String normalizedPhone = phoneVerificationUtil.normalizePhoneNumber(confirmDto.getPhoneNumber());

        // ✓ 명확한 예외 처리
        PhoneVerification verification = phoneVerificationRepo
                .findByPhoneAndVerificationCodeAndStatus(
                        normalizedPhone,
                        confirmDto.getVerificationCode(),
                        VerificationStatus.PENDING
                )
                .orElseThrow(() -> new MessageVerificationFailedException(ErrorCode.VERIFICATION_CODE_MISMATCH));

        // 만료 확인
        if (verification.isExpired()) {
            verification.markAsExpired();
            phoneVerificationRepo.save(verification);
            throw new MessageVerificationFailedException(ErrorCode.VERIFICATION_EXPIRED);
        }

        // 차단 확인
        if (verification.isBlocked()) {
            throw new MessageVerificationFailedException(ErrorCode.VERIFICATION_BLOCKED);
        }

        // 인증 성공
        verification.markAsVerified();
        phoneVerificationRepo.save(verification);

        log.info("본인 인증 완료 - 휴대폰: {}", normalizedPhone);
        return true;
    }

    /**
     * 이메일 수신 메시지에서 인증 정보 추출 및 검증
     */
    public void processReceivedMessage(String emailSender, String messageBody) {
        String extractedPhone = phoneVerificationUtil.extractPhoneNumberFromEmail(emailSender);
        if (extractedPhone == null) {
            log.warn("이메일에서 휴대폰 번호 추출 실패: {}", emailSender);
            return;
        }

        String normalizedPhone = phoneVerificationUtil.normalizePhoneNumber(extractedPhone);

        PhoneVerification verification = phoneVerificationRepo
                .findTopByPhoneOrderByCreatedAtDesc(normalizedPhone)
                .orElse(null);

        if (verification != null && verification.getStatus() == VerificationStatus.PENDING) {
            if (messageBody.contains(verification.getVerificationCode())) {
                verification.markAsVerified();
                verification.setSentMessage(messageBody);
                phoneVerificationRepo.save(verification);
                log.info("메시지 수신 확인 - 휴대폰: {}", normalizedPhone);
            }
        }
    }
}
