package com.competency.scms.service.auth;

import com.competency.scms.domain.user.LoginHistory;
import com.competency.scms.domain.user.User;
import com.competency.scms.domain.user.UserRole;
import com.competency.scms.dto.auth.LoginRequestDto;
import com.competency.scms.dto.auth.LoginResponseDto;
import com.competency.scms.exception.InvalidPasswordException;
import com.competency.scms.exception.UserNotFoundException;
import com.competency.scms.repository.user.LoginHistoryRepository;
import com.competency.scms.repository.user.PhoneVerificationRepository;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoginHistoryRepository loginHistoryRepository;

    @Mock
    private PhoneVerificationRepository phoneVerificationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private LoginRequestDto loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .userNum(20241001)
                .name("테스트학생")
                .email("test@student.com")
                .password("encodedPassword")
                .role(UserRole.STUDENT)
                .birthDate(LocalDate.of(2000, 1, 1))
                .locked(false)
                .failCnt(0)
                .build();

        loginRequest = LoginRequestDto.builder()
                .userNum(20241001)
                .password("password123")
                .build();
    }

    @Test
    void 로그인_성공() {
        // given
        when(userRepository.findByUserNum(20241001)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateAccessToken(1L, "test@student.com", "STUDENT")).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(1L, "test@student.com", "STUDENT")).thenReturn("refreshToken");

        // when
        LoginResponseDto response = userService.login(loginRequest, "127.0.0.1", "TestAgent");

        // then
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(response.getName()).isEqualTo("테스트학생");
        assertThat(response.getUserNum()).isEqualTo(20241001);
        assertThat(response.getMessage()).isEqualTo("로그인 성공");

        verify(loginHistoryRepository).save(any(LoginHistory.class));
        verify(userRepository).save(testUser); // 실패 횟수 초기화
    }

    @Test
    void 로그인_실패_사용자없음() {
        // given
        when(userRepository.findByUserNum(20241001)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest, "127.0.0.1", "TestAgent"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("등록된 사용자가 없습니다.");
    }

    @Test
    void 로그인_실패_비밀번호불일치() {
        // given
        when(userRepository.findByUserNum(20241001)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest, "127.0.0.1", "TestAgent"))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");

        verify(userRepository).save(testUser); // 실패 횟수 증가
        verify(loginHistoryRepository).save(any(LoginHistory.class)); // 실패 기록
    }

    @Test
    void 로그인_실패_계정잠금() {
        // given
        testUser = User.builder()
                .id(1L)
                .userNum(20241001)
                .name("테스트학생")
                .email("test@student.com")
                .password("encodedPassword")
                .role(UserRole.STUDENT)
                .birthDate(LocalDate.of(2000, 1, 1))
                .locked(true) // 잠금 상태
                .failCnt(5)
                .build();

        when(userRepository.findByUserNum(20241001)).thenReturn(Optional.of(testUser));

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest, "127.0.0.1", "TestAgent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("계정이 잠금되었습니다. 관리자에게 문의하세요.");

        verify(loginHistoryRepository).save(any(LoginHistory.class)); // 실패 기록
    }
}
