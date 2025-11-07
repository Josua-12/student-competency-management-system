package com.competency.SCMS.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneVerificationResponseDto {

    private String verificationCode;
    private String smsLink;
    private String phoneNumber;
    private String receiverEmail;
    private String expiresAt;
    private String message;
}
