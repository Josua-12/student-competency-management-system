package com.competency.scms.service.mail;

import com.competency.scms.domain.mail.EmailHistory;
import com.competency.scms.domain.user.User;
import com.competency.scms.repository.mail.EmailHistoryRepository;
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
    private final EmailHistoryRepository emailHistoryRepository;

    public void sendVerificationCode(String email, String code) {
        String subject = "비밀번호 찾기 인증번호";
        String content = "안녕하세요.\n\n비밀번호 찾기 인증번호입니다.\n\n인증번호: " + code + "\n\n10분 내에 입력해주세요.";

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("인증번호 이메일 발송 완료: {}", email);

            // 성공 시 이력 저장
            saveEmailHistory(null, email, subject, content, "PASSWORD_RESET", true, null);

        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", email, e);

            // 실패 시 이력 저장
            saveEmailHistory(null, email, subject, content, "PASSWORD_RESET", false, e.getMessage());

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

    private void saveEmailHistory(User user, String email, String subject,
                                  String content, String type, boolean success, String error) {
        try {
            EmailHistory history = EmailHistory.builder()
                    .user(user)
                    .recipientEmail(email)
                    .subject(subject)
                    .content(content)
                    .emailType(type)
                    .isSuccess(success)
                    .errorMessage(error)
                    .build();

            emailHistoryRepository.save(history);
            log.debug("이메일 발송 이력 저장 완료: {}", email);

        } catch (Exception e) {
            log.error("이메일 발송 이력 저장 실패: {}", email, e);
            // 이력 저장 실패는 메인 기능에 영향을 주지 않도록 예외를 던지지 않음
        }
    }
    /**
     * 프로그램 승인 알림 이메일
     */
    public void sendProgramApprovalNotification(String email, String programName, String status) {
        String subject = "[푸름대학교] 비교과 프로그램 신청 결과 안내";
        String content = buildProgramStatusContent(programName, status);
        sendNotificationEmail(email, subject, content, "PROGRAM_STATUS");
    }

    /**
     * 상담 승인/거절/취소 알림 이메일
     */
    public void sendCounselingStatusNotification(String email, String counselingType, String status, String date) {
        String subject = "[푸름대학교] 상담 예약 상태 변경 안내";
        String content = buildCounselingStatusContent(counselingType, status, date);
        sendNotificationEmail(email, subject, content, "COUNSELING_STATUS");
    }

    /**
     * 공통 알림 이메일 발송
     */
    private void sendNotificationEmail(String email, String subject, String content, String emailType) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("알림 이메일 발송 완료: {} - {}", emailType, email);

            saveEmailHistory(null, email, subject, content, emailType, true, null);

        } catch (Exception e) {
            log.error("알림 이메일 발송 실패: {} - {}", emailType, email, e);
            saveEmailHistory(null, email, subject, content, emailType, false, e.getMessage());
        }
    }

    /**
     * 프로그램 상태 변경 이메일 내용 생성
     */
    private String buildProgramStatusContent(String programName, String status) {
        String statusText = switch (status.toUpperCase()) {
            case "APPROVED" -> "승인되었습니다";
            case "REJECTED" -> "거절되었습니다";
            case "CANCELLED" -> "취소되었습니다";
            default -> "상태가 변경되었습니다";
        };

        return String.format("""
        안녕하세요. 푸름대학교입니다.
        
        신청하신 비교과 프로그램의 상태가 변경되었습니다.
        
        프로그램명: %s
        처리 결과: %s
        
        자세한 내용은 학생역량관리시스템에서 확인해주세요.
        
        감사합니다.
        """, programName, statusText);
    }

    /**
     * 상담 상태 변경 이메일 내용 생성
     */
    private String buildCounselingStatusContent(String counselingType, String status, String date) {
        String statusText = switch (status.toUpperCase()) {
            case "APPROVED" -> "승인되었습니다";
            case "REJECTED" -> "거절되었습니다";
            case "CANCELLED" -> "취소되었습니다";
            default -> "상태가 변경되었습니다";
        };

        return String.format("""
        안녕하세요. 푸름대학교입니다.
        
        예약하신 상담의 상태가 변경되었습니다.
        
        상담 유형: %s
        예약 일시: %s
        처리 결과: %s
        
        자세한 내용은 학생역량관리시스템에서 확인해주세요.
        
        감사합니다.
        """, counselingType, date, statusText);
    }
}
