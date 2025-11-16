package com.competency.scms.dto.user.mypage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeDto(
        @NotBlank(message = "현재 비밀번호를 입력해주세요.")
        String currentPassword,
        
        @NotBlank(message = "새 비밀번호를 입력해주세요.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        String newPassword,
        
        @NotBlank(message = "새 비밀번호 확인을 입력해주세요.")
        String confirmPassword
) {
    public boolean isPasswordMatched() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}