package com.competency.scms.controller.main;

import com.competency.scms.controller.DashboardController;
import com.competency.scms.domain.user.User;
import com.competency.scms.domain.user.UserRole;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private CustomUserDetails userDetails;

    @InjectMocks
    private DashboardController dashboardController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userNum(20240001)
                .name("테스트사용자")
                .email("test@example.com")
                .role(UserRole.STUDENT)
                .build();

        // getUserInfo 테스트 오류 수정 - 홍종학
        when(userDetails.getUser()).thenReturn(testUser);
    }

    @Test
    void 사용자정보_조회_성공_이메일() {
        // given
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // when
        ResponseEntity<Map<String, Object>> response = dashboardController.getUserInfo(userDetails);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().get("name")).isEqualTo("테스트사용자");
        assertThat(response.getBody().get("email")).isEqualTo("test@example.com");
    }

    @Test
    void 사용자정보_조회_성공_학번() {
        // given
        when(authentication.getName()).thenReturn("20240001");
        when(userRepository.findByUserNum(20240001)).thenReturn(Optional.of(testUser));

        // when
        ResponseEntity<Map<String, Object>> response = dashboardController.getUserInfo(userDetails);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().get("name")).isEqualTo("테스트사용자");
    }

    @Test
    void 사용자정보_조회_실패_사용자없음() {
        // given
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> dashboardController.getUserInfo(userDetails))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("사용자를 찾을 수 없습니다: test@example.com");
    }
}
