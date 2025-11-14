package com.competency.scms.repository.mail;

import com.competency.scms.domain.mail.EmailHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailHistoryRepository extends JpaRepository<EmailHistory, Long> {

    List<EmailHistory> findByRecipientOrderBySentAtDesc(String recipient);

    List<EmailHistory> findByEmailTypeAndSentAtBetween(String emailType, LocalDateTime start, LocalDateTime end);

    long countBySuccessAndSentAtBetween(Boolean success, LocalDateTime start, LocalDateTime end);
}
