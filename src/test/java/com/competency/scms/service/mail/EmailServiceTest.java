package com.competency.scms.service.mail;

import com.competency.scms.repository.mail.EmailHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private EmailHistoryRepository emailHistoryRepository;

    @InjectMocks
    private EmailService emailService;

    @Test
    void 인증번호_이메일_발송_성공() {
        // given
        String email = "test@example.com";
        String code = "123456";

        // when
        emailService.sendVerificationCode(email, code);

        // then
        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(emailHistoryRepository).save(any());
    }

    @Test
    void 인증번호_이메일_발송_실패() {
        // given
        String email = "test@example.com";
        String code = "123456";
        doThrow(new RuntimeException("메일 서버 오류")).when(mailSender).send(any(SimpleMailMessage.class));

        // when & then
        assertThatThrownBy(() -> emailService.sendVerificationCode(email, code))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이메일 발송에 실패했습니다.");

        verify(emailHistoryRepository).save(any());
    }

    @Test
    void 이메일_마스킹_정상() {
        // given
        String email = "testuser@example.com";

        // when
        String masked = emailService.maskEmail(email);

        // then
        assertThat(masked).isEqualTo("te***r@example.com");
    }

    @Test
    void 이메일_마스킹_짧은이메일() {
        // given
        String email = "ab@example.com";

        // when
        String masked = emailService.maskEmail(email);

        // then
        assertThat(masked).isEqualTo("ab@example.com");
    }

    @Test
    void 프로그램_승인_알림_발송() {
        // given
        String email = "test@example.com";
        String programName = "테스트프로그램";
        String status = "APPROVED";

        // when
        emailService.sendProgramApprovalNotification(email, programName, status);

        // then
        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(emailHistoryRepository).save(any());
    }

    @Test
    void 상담_상태_알림_발송() {
        // given
        String email = "test@example.com";
        String counselingType = "진로상담";
        String status = "APPROVED";
        String date = "2024-01-15 14:00";

        // when
        emailService.sendCounselingStatusNotification(email, counselingType, status, date);

        // then
        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(emailHistoryRepository).save(any());
    }
}
