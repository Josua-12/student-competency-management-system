package com.competency.scms.service.user;

import com.competency.scms.domain.user.User;
import com.competency.scms.dto.user.UserInfoResponseDto;
import com.competency.scms.dto.user.UserUpdateDto;
import com.competency.scms.exception.UserNotFoundException;
import com.competency.scms.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInfoService {
    
    private final UserRepository userRepository;
    
    public UserInfoResponseDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        return UserInfoResponseDto.from(user);
    }
    
    @Transactional
    public void updateUserInfo(Long userId, UserUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        
        user.updateInfo(dto.email(), dto.phone());
    }
}