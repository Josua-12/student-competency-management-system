package com.competency.scms.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneVerificationResponseDto {

    private boolean success;
    private String verificationCode;
    private String smsLink;
    private String phoneNumber;
    private String receiverEmail;
    private String expiresAt;
    private Long expiresIn;
    private String message;
}
