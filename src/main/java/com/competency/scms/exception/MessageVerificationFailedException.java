package com.competency.scms.exception;

public class MessageVerificationFailedException extends BusinessException {

    public MessageVerificationFailedException() {
        super(ErrorCode.VERIFICATION_CODE_MISMATCH);
    }

    public MessageVerificationFailedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MessageVerificationFailedException(String message) {
        super(ErrorCode.VERIFICATION_CODE_MISMATCH, message);
    }

    public MessageVerificationFailedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
