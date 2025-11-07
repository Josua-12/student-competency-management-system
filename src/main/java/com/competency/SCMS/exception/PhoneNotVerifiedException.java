package com.competency.SCMS.exception;

public class PhoneNotVerifiedException extends RuntimeException {
    public PhoneNotVerifiedException() {
        super("휴대폰 인증이 아직 완료되지 않았습니다.");
    }

    public PhoneNotVerifiedException(String message) {
        super(message);
    }
}
