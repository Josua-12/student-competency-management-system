package com.competency.scms.dto.auth;

import lombok.*;

/**
 * 로그인 응답 DTO
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    private Long userId;
    private Integer userNum;
    private String email;
    private String name;
    private String role;
    private String accessToken;
    private String refreshToken;
    private String message;
}
