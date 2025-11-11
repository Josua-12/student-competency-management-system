package com.competency.SCMS.exception;

public class InvalidPasswordException extends BusinessException {
  public InvalidPasswordException() {
    super(ErrorCode.INVALID_PASSWORD);
  }

  public InvalidPasswordException(String message) {
    super(ErrorCode.INVALID_PASSWORD, message);
  }
}
