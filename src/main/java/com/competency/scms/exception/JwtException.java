package com.competency.scms.exception;

public class JwtException extends BusinessException {
    public JwtException() {
        super(ErrorCode.TOKEN_INVALID);
    }

    public JwtException(String message) {
        super(ErrorCode.TOKEN_INVALID, message);
    }

    public JwtException(ErrorCode errorCode) {
        super(errorCode);
    }

    public JwtException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
