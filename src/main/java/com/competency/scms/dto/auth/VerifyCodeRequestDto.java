package com.competency.scms.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyCodeRequestDto {

    @NotBlank(message = "학번은 필수입니다")
    private String userNum;

    @NotBlank(message = "휴대폰 번호는 필수입니다")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{4}-[0-9]{4}$")
    private String phoneNumber;

    @NotBlank(message = "인증번호는 필수입니다")
    @Size(min = 6, max = 6, message = "인증번호는 6자리입니다")
    private String verificationCode;
}
