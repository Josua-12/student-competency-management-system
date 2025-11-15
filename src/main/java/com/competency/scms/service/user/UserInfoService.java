package com.competency.scms.service.user;

import com.competency.scms.domain.user.User;
import com.competency.scms.dto.user.mypage.PasswordChangeDto;
import com.competency.scms.dto.user.UserInfoResponseDto;
import com.competency.scms.dto.user.UserUpdateDto;
import com.competency.scms.exception.UserNotFoundException;
import com.competency.scms.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInfoService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserInfoResponseDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        return UserInfoResponseDto.from(user);
    }
    
    @Transactional
    public void updateUserInfo(Long userId, UserUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        
        if (dto.email() != null) {
            user.updateEmail(dto.email());
        }
        if (dto.phone() != null) {
            user.updatePhone(dto.phone());
        }
        if (dto.address() != null) {
            user.updateAddress(dto.address());
        }
    }

    @Transactional
    public void changePassword(Long userId, PasswordChangeDto dto) {
        if (!dto.isPasswordMatched()) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        // 새 비밀번호로 변경
        user.updatePassword(passwordEncoder.encode(dto.newPassword()));
    }
}