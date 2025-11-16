// src/test/java/com/competency/scms/service/mail/EmailServiceIntegrationTest.java (수정)
package com.competency.scms.service.mail;

import com.competency.scms.domain.mail.EmailHistory;
import com.competency.scms.repository.mail.EmailHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@MockitoSettings
class EmailServiceIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailHistoryRepository emailHistoryRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    @Test
    @DisplayName("프로그램 승인 이메일 발송 시 이력 저장 확인")
    void programApprovalEmailHistorySaved() {
        // given
        String testEmail = "program-test@example.com";
        String programName = "AI 프로그래밍 워크샵";
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // when
        emailService.sendProgramApprovalNotification(testEmail, programName, "APPROVED");

        // then
        List<EmailHistory> histories = emailHistoryRepository.findByRecipientEmailOrderBySentAtDesc(testEmail);
        assertThat(histories).hasSize(1);

        EmailHistory history = histories.get(0);
        assertThat(history.getRecipientEmail()).isEqualTo(testEmail);
        assertThat(history.getEmailType()).isEqualTo("PROGRAM_STATUS");
        assertThat(history.getIsSuccess()).isTrue();
    }

    @Test
    @DisplayName("이메일 발송 실패 시 실패 이력 저장 확인")
    void emailFailureHistorySaved() {
        // given
        String testEmail = "failure-test@example.com";
        String code = "123456";
        doThrow(new RuntimeException("SMTP 연결 실패")).when(mailSender).send(any(SimpleMailMessage.class));

        // when
        try {
            emailService.sendVerificationCode(testEmail, code);
        } catch (RuntimeException e) {
            // 예외 무시
        }

        // then
        List<EmailHistory> histories = emailHistoryRepository.findByRecipientEmailOrderBySentAtDesc(testEmail);
        assertThat(histories).hasSize(1);

        EmailHistory history = histories.get(0);
        assertThat(history.getIsSuccess()).isFalse();
        assertThat(history.getErrorMessage()).contains("SMTP 연결 실패");
    }
}
