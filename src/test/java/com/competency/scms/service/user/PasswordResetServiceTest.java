package com.competency.scms.service.user;

import com.competency.scms.domain.user.PhoneVerification;
import com.competency.scms.domain.user.User;
import com.competency.scms.domain.user.UserRole;
import com.competency.scms.domain.user.VerificationStatus;
import com.competency.scms.dto.auth.*;
import com.competency.scms.exception.BusinessException;
import com.competency.scms.exception.ErrorCode;
import com.competency.scms.repository.user.PhoneVerificationRepository;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.service.mail.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PhoneVerificationRepository phoneVerificationRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User testUser;
    private PhoneVerification testVerification;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userNum(20240001)
                .name("테스트사용자")
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.STUDENT)
                .build();

        testVerification = PhoneVerification.builder()
                .user(testUser)
                .phone("test@example.com")
                .receiverEmail("test@example.com")
                .verificationCode("123456")
                .status(VerificationStatus.PENDING)
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();
    }

    @Test
    void 사용자_확인_성공() {
        // given
        VerifyUserRequestDto request = new VerifyUserRequestDto();
        request.setUserNum("20240001");
        request.setUserName("테스트사용자");

        when(userRepository.findByUserNumAndName(20240001, "테스트사용자"))
                .thenReturn(Optional.of(testUser));
        when(emailService.maskEmail("test@example.com")).thenReturn("te***t@example.com");

        // when
        VerifyUserResponseDto response = passwordResetService.verifyUser(request);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getUserNum()).isEqualTo("20240001");
        assertThat(response.getUserName()).isEqualTo("테스트사용자");
        verify(emailService).sendVerificationCode(eq("test@example.com"), anyString());
        verify(phoneVerificationRepository).save(any(PhoneVerification.class));
    }

    @Test
    void 사용자_확인_실패_사용자없음() {
        // given
        VerifyUserRequestDto request = new VerifyUserRequestDto();
        request.setUserNum("99999999");
        request.setUserName("없는사용자");

        when(userRepository.findByUserNumAndName(99999999, "없는사용자"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> passwordResetService.verifyUser(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void 인증번호_검증_성공() {
        // given
        VerifyCodeRequestDto request = new VerifyCodeRequestDto();
        request.setUserNum("20240001");
        request.setVerificationCode("123456");

        when(userRepository.findByUserNum(20240001)).thenReturn(Optional.of(testUser));
        when(phoneVerificationRepository.findTopByPhoneAndVerificationCodeAndStatusOrderByCreatedAtDesc(
                "test@example.com", "123456", VerificationStatus.PENDING))
                .thenReturn(Optional.of(testVerification));

        // when
        VerifyCodeResponseDto response = passwordResetService.verifyCode(request);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getToken()).contains("20240001:123456");
        verify(phoneVerificationRepository).save(testVerification);
        assertThat(testVerification.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    void 인증번호_검증_실패_코드불일치() {
        // given
        VerifyCodeRequestDto request = new VerifyCodeRequestDto();
        request.setUserNum("20240001");
        request.setVerificationCode("999999");

        when(userRepository.findByUserNum(20240001)).thenReturn(Optional.of(testUser));
        when(phoneVerificationRepository.findTopByPhoneAndVerificationCodeAndStatusOrderByCreatedAtDesc(
                "test@example.com", "999999", VerificationStatus.PENDING))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> passwordResetService.verifyCode(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VERIFICATION_CODE_MISMATCH);
    }

    @Test
    void 비밀번호_재설정_성공() {
        // given
        ResetPasswordRequestDto request = new ResetPasswordRequestDto();
        request.setToken("20240001:123456");
        request.setNewPassword("newPassword123");

        testVerification.setStatus(VerificationStatus.VERIFIED);

        when(userRepository.findByUserNum(20240001)).thenReturn(Optional.of(testUser));
        when(phoneVerificationRepository.findTopByPhoneAndStatusOrderByCreatedAtDesc(
                "test@example.com", VerificationStatus.VERIFIED))
                .thenReturn(Optional.of(testVerification));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");

        // when
        ResetPasswordResponseDto response = passwordResetService.resetPassword(request);

        // then
        assertThat(response.isSuccess()).isTrue();
        verify(userRepository).save(testUser);
        verify(phoneVerificationRepository).save(testVerification);
        assertThat(testVerification.getStatus()).isEqualTo(VerificationStatus.USED);
    }

    @Test
    void 비밀번호_재설정_실패_잘못된토큰() {
        // given
        ResetPasswordRequestDto request = new ResetPasswordRequestDto();
        request.setToken("invalid-token");
        request.setNewPassword("newPassword123");

        // when & then
        assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT);
    }
}
