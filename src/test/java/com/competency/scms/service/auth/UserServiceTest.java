package com.competency.scms.service.auth;

import com.competency.scms.domain.user.User;
import com.competency.scms.domain.user.UserRole;
import com.competency.scms.dto.auth.LoginRequestDto;
import com.competency.scms.dto.auth.LoginResponseDto;
import com.competency.scms.exception.BusinessException;
import com.competency.scms.exception.ErrorCode;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

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
                .userNum(20212024)
                .email("test@school.edu")
                .name("테스트")
                .password("encodedPassword")
                .role(UserRole.STUDENT)
                .build();

        loginRequest = new LoginRequestDto();
        loginRequest.setUserNum(20212024);
        loginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        given(userRepository.findByUserNum(20212024))
                .willReturn(Optional.of(testUser));
        given(passwordEncoder.matches("password123", "encodedPassword"))
                .willReturn(true);
        given(jwtUtil.generateAccessToken(1L, "test@school.edu", "STUDENT"))
                .willReturn("accessToken");
        given(jwtUtil.generateRefreshToken(1L, "test@school.edu", "STUDENT"))
                .willReturn("refreshToken");

        // when
        LoginResponseDto response = userService.login(loginRequest, "127.0.0.1", "TestAgent");

        // then
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("테스트");
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_UserNotFound() {
        // given
        given(userRepository.findByUserNum(20212024))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest, "127.0.0.1", "TestAgent"))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_InvalidPassword() {
        // given
        given(userRepository.findByUserNum(20212024))
                .willReturn(Optional.of(testUser));
        given(passwordEncoder.matches("password123", "encodedPassword"))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest, "127.0.0.1", "TestAgent"))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD);
    }
}
