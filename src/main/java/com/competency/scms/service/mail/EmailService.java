package com.competency.scms.service.mail;

import com.competency.scms.domain.mail.EmailHistory;
import com.competency.scms.repository.mail.EmailHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailHistoryRepository emailHistoryRepository;

    public void sendEmail(String to, String subject, String content, String emailType) {
        EmailHistory history = EmailHistory.builder()
                .recipient(to)
                .subject(subject)
                .content(content)
                .emailType(emailType)
                .sentAt(LocalDateTime.now())
                .build();

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            message.setFrom("noreply@pureum.ac.kr");

            mailSender.send(message);

            history.setSuccess(true);
            log.info("이메일 발송 성공: {} -> {}", emailType, to);

        } catch (Exception e) {
            history.setSuccess(false);
            history.setErrorMessage(e.getMessage());
            log.error("이메일 발송 실패: {} -> {}", emailType, to, e);
        } finally {
            emailHistoryRepository.save(history);
        }
    }

    // 프로그램 상태 변경 이메일
    public void sendProgramStatusEmail(String email, String name, String programTitle, String status) {
        String subject = "[푸름대학교] 비교과 프로그램 신청 결과";
        String content = switch (status) {
            case "APPROVED" -> String.format(
                    "%s님,\n\n" +
                            "신청하신 '%s' 프로그램이 승인되었습니다.\n\n" +
                            "프로그램 일정을 확인하고 참여해 주세요.\n\n" +
                            "푸름대학교 학생역량관리센터",
                    name, programTitle
            );
            case "REJECTED" -> String.format(
                    "%s님,\n\n" +
                            "신청하신 '%s' 프로그램이 반려되었습니다.\n\n" +
                            "자세한 사항은 학생역량관리센터로 문의해 주세요.\n\n" +
                            "푸름대학교 학생역량관리센터",
                    name, programTitle
            );
            case "CANCELED" -> String.format(
                    "%s님,\n\n" +
                            "신청하신 '%s' 프로그램이 취소되었습니다.\n\n" +
                            "푸름대학교 학생역량관리센터",
                    name, programTitle
            );
            default -> "";
        };

        sendEmail(email, subject, content, "PROGRAM_" + status);
    }

    // 상담 상태 변경 이메일
    public void sendCounselingStatusEmail(String email, String name, String date, String status) {
        String subject = "[푸름대학교] 상담 예약 결과";
        String content = switch (status) {
            case "APPROVED" -> String.format(
                    "%s님,\n\n" +
                            "%s 상담이 확정되었습니다.\n\n" +
                            "시간에 맞춰 참석해 주시기 바랍니다.\n\n" +
                            "푸름대학교 학생상담센터",
                    name, date
            );
            case "REJECTED" -> String.format(
                    "%s님,\n\n" +
                            "%s 상담 예약이 반려되었습니다.\n\n" +
                            "다른 시간으로 다시 예약해 주시기 바랍니다.\n\n" +
                            "푸름대학교 학생상담센터",
                    name, date
            );
            case "CANCELLED" -> String.format(
                    "%s님,\n\n" +
                            "%s 상담 예약이 취소되었습니다.\n\n" +
                            "푸름대학교 학생상담센터",
                    name, date
            );
            default -> "";
        };

        sendEmail(email, subject, content, "COUNSELING_" + status);
    }
}
