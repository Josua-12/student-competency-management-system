package com.competency.SCMS.exception;

/**
 * 모든 에러 코드를 한곳에서 관리
 * 각 예외의 code와 message를 정의
 */
public enum ErrorCode {

    // ========== 사용자 (USER) ==========
    USER_NOT_FOUND("USER-001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL("USER-002", "이미 존재하는 이메일입니다."),
    DUPLICATE_STUDENT_NUM("USER-003", "이미 존재하는 학번입니다."),
    INVALID_PASSWORD("USER-004", "비밀번호가 일치하지 않습니다."),
    USER_LOCKED("USER-005", "잠금 처리된 계정입니다."),

    // ========== 검증 (VALIDATION) ==========
    INVALID_INPUT("VALID-001", "입력값이 올바르지 않습니다."),
    REQUIRED_FIELD_MISSING("VALID-002", "필수 입력값이 누락되었습니다."),

    // ========== 권한 (AUTHORIZATION) ==========
    FORBIDDEN("AUTH-001", "접근 권한이 없습니다."),
    
    // ========== 입력값 (INPUT) ==========
    INVALID_INPUT_VALUE("INPUT-001", "잘못된 입력값입니다."),
    
    // ========== 시스템 (SYSTEM) ==========
    INTERNAL_SERVER_ERROR("SYS-001", "서버 오류가 발생했습니다."),
    DATABASE_ERROR("SYS-002", "데이터베이스 오류가 발생했습니다.");

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
