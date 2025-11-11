package com.competency.SCMS.dto.user;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneVerificationRequestDto {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotNull(message = "학번은 필수입니다.")
    private Integer userNum;

    @NotBlank(message = "생년월일은 필수입니다. (YYMMDD)")
    @Pattern(regexp = "^\\d{6}$", message = "생년월일은 YYMMDD 형식이어야 합니다.")
    private String birthDate;

    @NotBlank(message = "휴대폰 번호는 필수입니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
            message = "올바른 휴대폰 번호 형식이 아닙니다.")
    private String phone;

    @NotBlank(message = "인증 용도는 필수입니다.")
    private String purpose;
}
