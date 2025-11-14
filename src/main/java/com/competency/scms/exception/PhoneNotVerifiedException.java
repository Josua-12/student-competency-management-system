package com.competency.scms.exception;

public class PhoneNotVerifiedException extends BusinessException {
    public PhoneNotVerifiedException() {
        super(ErrorCode.PHONE_NOT_VERIFIED);
    }

    public PhoneNotVerifiedException(String message) {
        super(ErrorCode.PHONE_NOT_VERIFIED, message);
    }
}
