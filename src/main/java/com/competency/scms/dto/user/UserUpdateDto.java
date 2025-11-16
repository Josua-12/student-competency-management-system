package com.competency.scms.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateDto(
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다.")
        String email,

        @Size(max = 20, message = "전화번호는 20자를 초과할 수 없습니다.")
        String phone,

        @Size(max = 200, message = "주소는 200자를 초과할 수 없습니다.")
        String address
) { }