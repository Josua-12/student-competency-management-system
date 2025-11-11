package com.competency.SCMS.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendVerificationResponseDto {
    private boolean success;
    private String message;
    private int expiresIn; // 초 단위
}
