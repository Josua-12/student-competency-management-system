package com.competency.scms.event;

import com.competency.scms.service.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailEventListener {

    private final EmailService emailService;

    @EventListener
    @Async
    public void handleStatusChange(StatusChangeEvent event) {
        try {
            if ("PROGRAM".equals(event.getType())) {
                emailService.sendProgramApprovalNotification(
                        event.getEmail(),
                        event.getItemName(),
                        event.getStatus()
                );
            } else if ("COUNSELING".equals(event.getType())) {
                emailService.sendCounselingStatusNotification(
                        event.getEmail(),
                        event.getItemName(),
                        event.getStatus(),
                        event.getDate()
                );
            }
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", event.getEmail(), e);
        }
    }
}
