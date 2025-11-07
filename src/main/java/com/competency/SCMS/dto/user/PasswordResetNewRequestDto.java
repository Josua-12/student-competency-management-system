package com.competency.SCMS.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetNewRequestDto {
    @NotBlank(message = "휴대폰 번호는 필수입니다.")
    private String phone;

    @NotBlank(message = "인증 번호는 필수입니다.")
    private String verificationCode;

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;

    @NotBlank(message = "새 비밀번호 확인은 필수입니다.")
    private String confirmPassword;
}
