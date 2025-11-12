package com.competency.SCMS.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyCodeResponseDto {
    private boolean success;
    private String message;
    private String token; // 비밀번호 재설정 토큰
}
