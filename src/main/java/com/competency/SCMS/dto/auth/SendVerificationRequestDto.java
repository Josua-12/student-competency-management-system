package com.competency.SCMS.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SendVerificationRequestDto {

    @NotBlank(message = "학번은 필수입니다")
    private String userNum;

    @NotBlank(message = "휴대폰 번호는 필수입니다")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{4}-[0-9]{4}$",
            message = "휴대폰 번호 형식이 올바르지 않습니다")
    private String phoneNumber;
}
