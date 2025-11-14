package com.competency.scms.exception;

import com.competency.scms.global.response.ErrorResponse;
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
        log.error("BusinessException: {}", ex.getErrorCode().getCode());
        ErrorResponse response = ErrorResponse.of(ex.getErrorCode());

        // ErrorCode에 따라 HTTP 상태 코드 매핑
        HttpStatus status = mapErrorCodeToHttpStatus(ex.getErrorCode());
        return ResponseEntity.status(status).body(response);
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
     * MessageVerificationFailedException 처리
     */
    @ExceptionHandler(MessageVerificationFailedException.class)
    public ResponseEntity<ErrorResponse> handleMessageVerificationFailed(
            MessageVerificationFailedException ex) {
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
                ErrorResponse.of(ex.getErrorCode())
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

    /**
     * ErrorCode를 HTTP 상태 코드로 매핑
     */
    private HttpStatus mapErrorCodeToHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case USER_NOT_FOUND, RESERVATION_NOT_FOUND, RECORD_NOT_FOUND,
                 QUESTION_NOT_FOUND, OPTION_NOT_FOUND, CATEGORY_NOT_FOUND,
                 COUNSELOR_NOT_FOUND -> HttpStatus.NOT_FOUND;

            case DUPLICATE_EMAIL, DUPLICATE_USER_NUM,
                 PHONE_ALREADY_VERIFIED, SATISFACTION_ALREADY_SUBMITTED -> HttpStatus.CONFLICT;

            case INVALID_PASSWORD, INVALID_CREDENTIALS, ACCOUNT_LOCKED,
                 PHONE_NOT_VERIFIED, TOKEN_EXPIRED, TOKEN_INVALID -> HttpStatus.UNAUTHORIZED;

            case VERIFICATION_EXPIRED, PHONE_NUMBER_MISMATCH -> HttpStatus.GONE;

            case FORBIDDEN -> HttpStatus.FORBIDDEN;

            case INVALID_INPUT, REQUIRED_FIELD_MISSING, INVALID_INPUT_VALUE,
                 MESSAGE_NOT_RECEIVED, VERIFICATION_CODE_MISMATCH,
                 VERIFICATION_BLOCKED, PASSWORD_MISMATCH,
                 PASSWORD_RESET_TOKEN_INVALID, PASSWORD_RESET_TOKEN_EXPIRED,
                 INVALID_RESERVATION_STATUS, CANNOT_MODIFY_SYSTEM_QUESTION,
                 CANNOT_DELETE_SYSTEM_QUESTION -> HttpStatus.BAD_REQUEST;

            case INTERNAL_SERVER_ERROR, DATABASE_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;

            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
