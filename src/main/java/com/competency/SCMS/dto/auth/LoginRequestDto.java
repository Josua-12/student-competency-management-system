package com.competency.SCMS.dto.auth;

import lombok.*;

/**
 * 로그인 요청 DTO
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {

    private String email;
    private String password;
    private Integer studentNum;
}
