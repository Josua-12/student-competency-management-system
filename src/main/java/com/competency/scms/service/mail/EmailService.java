package com.competency.scms.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("비밀번호 찾기 인증번호");
            message.setText("안녕하세요.\n\n비밀번호 찾기 인증번호입니다.\n\n인증번호: " + code + "\n\n10분 내에 입력해주세요.");

            mailSender.send(message);
            log.info("인증번호 이메일 발송 완료: {}", email);
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }
    }

    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 2) {
            return email;
        }

        String masked = localPart.substring(0, 2) + "***" + localPart.substring(localPart.length() - 1);
        return masked + "@" + domain;
    }
}
