package com.competency.SCMS.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyUserResponseDto {
    private boolean success;
    private String message;
    private String userNum;
    private String userName;
}
