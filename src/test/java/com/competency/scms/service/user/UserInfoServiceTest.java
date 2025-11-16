package com.competency.scms.service.user;

import com.competency.scms.domain.user.User;
import com.competency.scms.dto.user.UserUpdateDto;
import com.competency.scms.exception.UserNotFoundException;
import com.competency.scms.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserInfoService userInfoService;

    @Test
    void updateUserInfo_성공() {
        // given
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("old@example.com")
                .phone("010-0000-0000")
                .build();
        
        UserUpdateDto dto = new UserUpdateDto("new@example.com", "010-1234-5678");
        
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userInfoService.updateUserInfo(userId, dto);

        // then
        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getPhone()).isEqualTo("010-1234-5678");
    }

    @Test
    void updateUserInfo_사용자없음() {
        // given
        Long userId = 1L;
        UserUpdateDto dto = new UserUpdateDto("new@example.com", "010-1234-5678");
        
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userInfoService.updateUserInfo(userId, dto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }
}