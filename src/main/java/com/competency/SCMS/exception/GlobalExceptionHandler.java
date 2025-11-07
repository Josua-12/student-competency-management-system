package com.competency.SCMS.exception;

import com.competency.SCMS.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * BusinessException 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.error("BusinessException: {}", ex.getMessage());
        ErrorResponse response = ErrorResponse.of(ex.getErrorCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * UserNotFoundException 처리
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        log.error("UserNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.of(ex.getErrorCode())
        );
    }

    /**
     * InvalidPasswordException 처리
     */
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(InvalidPasswordException ex) {
        log.error("InvalidPasswordException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.of(ex.getErrorCode())
        );
    }

    /**
     * DuplicateEmailException 처리
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex) {
        log.error("DuplicateEmailException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.of(ex.getErrorCode())
        );
    }

    /**
     * PhoneNotVerifiedException 처리
     */
    @ExceptionHandler(PhoneNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handlePhoneNotVerified(PhoneNotVerifiedException ex) {
        log.error("PhoneNotVerifiedException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.of(ex.getErrorCode())
        );
    }

    /**
     * PhoneExpiredException 처리
     */
    @ExceptionHandler(PhoneExpiredException.class)
    public ResponseEntity<ErrorResponse> handlePhoneExpired(PhoneExpiredException ex) {
        log.error("PhoneExpiredException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.GONE).body(
                ErrorResponse.of(ex.getErrorCode())
        );
    }

    /**
     * MessageVerificationFailedException 처리 (새로 추가)
     */
    @ExceptionHandler(MessageVerificationFailedException.class)
    public ResponseEntity<ErrorResponse> handleMessageVerificationFailed(MessageVerificationFailedException ex) {
        log.error("MessageVerificationFailedException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.of(ex.getErrorCode())
        );
    }

    /**
     * JwtException 처리
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex) {
        log.error("JwtException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.of(ErrorCode.TOKEN_INVALID, ex.getMessage())
        );
    }

    /**
     * @Valid 검증 실패 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * 기타 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage())
        );
    }
}
