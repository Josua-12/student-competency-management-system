package com.competency.SCMS.exception;

public class PhoneExpiredException extends RuntimeException {
    public PhoneExpiredException() {
        super("인증 코드가 만료되었습니다. 다시 요청해주세요.");
    }

    public PhoneExpiredException(String message) {
        super(message);
    }
}
