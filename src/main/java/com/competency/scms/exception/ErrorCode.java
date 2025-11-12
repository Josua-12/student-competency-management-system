package com.competency.scms.exception;

/**
 * 모든 에러 코드를 한 곳에서 관리
 * 각 예외의 code와 message를 정의
 */
public enum ErrorCode {

    // ========== 사용자 (USER) ==========
    USER_NOT_FOUND("USER-001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL("USER-002", "이미 존재하는 이메일입니다."),
    DUPLICATE_USER_NUM("USER-003", "이미 존재하는 학번입니다."),
    INVALID_PASSWORD("USER-004", "비밀번호가 일치하지 않습니다."),
    USER_LOCKED("USER-005", "잠금 처리된 계정입니다."),

    // ========== 본인인증 (VERIFICATION) ==========
    MESSAGE_NOT_RECEIVED("VERIFY-001", "메시지가 수신되지 않았습니다."),
    VERIFICATION_CODE_MISMATCH("VERIFY-002", "인증 코드가 일치하지 않습니다."),
    VERIFICATION_EXPIRED("VERIFY-003", "인증 코드가 만료되었습니다."),
    PHONE_NUMBER_MISMATCH("VERIFY-004", "휴대폰 번호가 일치하지 않습니다."),
    VERIFICATION_BLOCKED("VERIFY-005", "인증 시도 횟수를 초과하여 차단되었습니다."),
    PHONE_ALREADY_VERIFIED("VERIFY-006", "이미 인증된 휴대폰 번호입니다."),
    PHONE_NOT_VERIFIED("VERIFY-007", "휴대폰 인증이 완료되지 않았습니다."),

    // ========== 로그인 (LOGIN) ==========
    INVALID_CREDENTIALS("LOGIN-001", "이메일 또는 비밀번호가 올바르지 않습니다."),
    ACCOUNT_LOCKED("LOGIN-002", "계정이 잠금 상태입니다."),
    TOKEN_EXPIRED("LOGIN-003", "토큰이 만료되었습니다."),
    TOKEN_INVALID("LOGIN-004", "유효하지 않은 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND("LOGIN-005", "리프레시 토큰이 없습니다."),
    INVALID_REFRESH_TOKEN("LOGIN-006", "유효하지 않은 리프레시 토큰입니다."),

    // ========== 비밀번호 재설정 (PASSWORD RESET) ==========
    PASSWORD_RESET_TOKEN_INVALID("PWD-001", "유효하지 않은 비밀번호 재설정 토큰입니다."),
    PASSWORD_RESET_TOKEN_EXPIRED("PWD-002", "비밀번호 재설정 토큰이 만료되었습니다."),
    PASSWORD_MISMATCH("PWD-003", "비밀번호가 일치하지 않습니다."),

    // ========== 검증 (VALIDATION) ==========
    INVALID_INPUT("VALID-001", "입력값이 올바르지 않습니다."),
    REQUIRED_FIELD_MISSING("VALID-002", "필수 입력값이 누락되었습니다."),
    INVALID_REQUEST("COMMON-400", "잘못된 요청입니다."),

    // ========== 권한 (AUTHORIZATION) ==========
    FORBIDDEN("AUTH-001", "접근 권한이 없습니다."),
    
    // ========== 입력값 (INPUT) ==========
    INVALID_INPUT_VALUE("INPUT-001", "잘못된 입력값입니다."),
    
    // ========== 시스템 (SYSTEM) ==========
    INTERNAL_SERVER_ERROR("SYS-001", "서버 오류가 발생했습니다."),
    DATABASE_ERROR("SYS-002", "데이터베이스 오류가 발생했습니다."),
    
    // ========== 상담 (COUNSELING) ==========
    RESERVATION_NOT_FOUND("CNSL-001", "상담 예약을 찾을 수 없습니다."),
    INVALID_RESERVATION_STATUS("CNSL-002", "유효하지 않은 예약 상태입니다."),
    RECORD_NOT_FOUND("CNSL-003", "상담일지를 찾을 수 없습니다."),
    SATISFACTION_ALREADY_SUBMITTED("CNSL-004", "이미 만족도 조사를 제출했습니다."),
    QUESTION_NOT_FOUND("CNSL-005", "설문 문항을 찾을 수 없습니다."),
    OPTION_NOT_FOUND("CNSL-006", "설문 옵션을 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND("CNSL-007", "상담 분류를 찾을 수 없습니다."),
    COUNSELOR_NOT_FOUND("CNSL-008", "상담사를 찾을 수 없습니다."),
    CANNOT_MODIFY_SYSTEM_QUESTION("CNSL-009", "시스템 기본 질문은 수정할 수 없습니다."),
    CANNOT_DELETE_SYSTEM_QUESTION("CNSL-010", "시스템 기본 질문은 삭제할 수 없습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
