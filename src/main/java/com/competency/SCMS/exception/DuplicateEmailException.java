package com.competency.SCMS.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("이미 존재하는 이메일입니다.");
    }

    public DuplicateEmailException(String message) {
        super(message);
    }
}
