package com.competency.scms.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyUserRequestDto {

    @NotBlank(message = "학번은 필수입니다")
    private String userNum;

    @NotBlank(message = "이름은 필수입니다")
    private String userName;
}
