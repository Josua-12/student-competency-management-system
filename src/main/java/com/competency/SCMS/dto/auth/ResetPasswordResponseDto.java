package com.competency.SCMS.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordResponseDto {
    private boolean success;
    private String message;
}
