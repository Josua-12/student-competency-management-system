package com.competency.scms.exception;

public class PhoneExpiredException extends BusinessException {
    public PhoneExpiredException() {
        super(ErrorCode.VERIFICATION_EXPIRED);
    }

    public PhoneExpiredException(String message) {
        super(ErrorCode.VERIFICATION_EXPIRED, message);
    }
}
