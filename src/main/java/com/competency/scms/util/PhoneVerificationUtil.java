package com.competency.SCMS.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PhoneVerificationUtil {

    @Value("${phone.verification.email:verification@scms.com}")
    private String verificationEmail;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\d{10,11})@.*");

    /**
     * 6자리 랜덤 인증 코드 생성
     */
    public String generateVerificationCode() {
        int code = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * SMS 링크 생성 (sms:email@domain.com?body=123456)
     */
    public String generateSmsLink(String verificationCode) {
        return String.format("sms:%s?body=%s", verificationEmail, verificationCode);
    }

    /**
     * 이메일에서 휴대폰 번호 추출 (010xxxxxxxx@email.com → 010xxxxxxxx)
     */
    public String extractPhoneNumberFromEmail(String emailSender) {
        if (emailSender == null) return null;
        Matcher matcher = PHONE_PATTERN.matcher(emailSender);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 휴대폰 번호 포맷 정규화 (010-1234-5678 → 01012345678)
     */
    public String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return null;
        return phoneNumber.replaceAll("[^0-9]", "");
    }

    /**
     * 휴대폰 번호 포맷 검증
     */
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return false;
        String normalized = normalizePhoneNumber(phoneNumber);
        return normalized.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$");
    }

    public String getVerificationEmail() {
        return verificationEmail;
    }
}
