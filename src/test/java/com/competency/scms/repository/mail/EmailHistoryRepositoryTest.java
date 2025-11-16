// src/test/java/com/competency/scms/repository/mail/EmailHistoryRepositoryTest.java
package com.competency.scms.repository.mail;

import com.competency.scms.domain.mail.EmailHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmailHistoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmailHistoryRepository emailHistoryRepository;

    @Test
    @DisplayName("이메일 주소로 발송 이력 조회")
    void findByRecipientEmailOrderBySentAtDesc() {
        // given
        String email = "test@example.com";
        EmailHistory history1 = createEmailHistory(email, "PASSWORD_RESET", true);
        EmailHistory history2 = createEmailHistory(email, "PROGRAM_STATUS", true);

        entityManager.persistAndFlush(history1);
        entityManager.persistAndFlush(history2);

        // when
        List<EmailHistory> results = emailHistoryRepository.findByRecipientEmailOrderBySentAtDesc(email);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getSentAt()).isAfter(results.get(1).getSentAt());
    }

    @Test
    @DisplayName("이메일 타입과 기간으로 조회")
    void findByEmailTypeAndSentAtBetween() {
        // given
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        EmailHistory history = createEmailHistory("test@example.com", "PASSWORD_RESET", true);
        entityManager.persistAndFlush(history);

        // when
        List<EmailHistory> results = emailHistoryRepository.findByEmailTypeAndSentAtBetween("PASSWORD_RESET", start, end);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEmailType()).isEqualTo("PASSWORD_RESET");
    }

    @Test
    @DisplayName("성공/실패 건수 조회")
    void countByIsSuccessAndSentAtBetween() {
        // given
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        EmailHistory success = createEmailHistory("test1@example.com", "PASSWORD_RESET", true);
        EmailHistory failure = createEmailHistory("test2@example.com", "PASSWORD_RESET", false);

        entityManager.persistAndFlush(success);
        entityManager.persistAndFlush(failure);

        // when
        long successCount = emailHistoryRepository.countByIsSuccessAndSentAtBetween(true, start, end);
        long failureCount = emailHistoryRepository.countByIsSuccessAndSentAtBetween(false, start, end);

        // then
        assertThat(successCount).isEqualTo(1);
        assertThat(failureCount).isEqualTo(1);
    }

    private EmailHistory createEmailHistory(String email, String type, boolean success) {
        return EmailHistory.builder()
                .recipientEmail(email)
                .subject("테스트 제목")
                .content("테스트 내용")
                .emailType(type)
                .isSuccess(success)
                .sentAt(LocalDateTime.now())
                .build();
    }
}
