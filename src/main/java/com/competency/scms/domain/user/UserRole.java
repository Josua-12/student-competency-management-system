// src/main/java/com/competency/scms/domain/user/UserRole.java (수정)
package com.competency.scms.domain.user;

public enum UserRole {
    // 학생
    STUDENT("학생"),

    // 상담 관련
    COUNSELOR("상담사"),
    COUNSELING_ADMIN("상담 관리자"),

    // 비교과 관련
    NONCURRICULAR_OPERATOR("비교과 운영자"),
    NONCURRICULAR_ADMIN("비교과 관리자"),

    // 역량 관련
    COMPETENCY_ADMIN("역량 관리자"),

    // 최고 관리자
    SUPER_ADMIN("최고 관리자");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // 권한 체크 헬퍼 메서드
    public boolean canAccessCounseling() {
        return this == COUNSELOR || this == COUNSELING_ADMIN || this == SUPER_ADMIN;
    }

    public boolean canAccessNoncurricular() {
        return this == NONCURRICULAR_OPERATOR || this == NONCURRICULAR_ADMIN || this == SUPER_ADMIN;
    }

    public boolean canAccessCompetency() {
        return this == COMPETENCY_ADMIN || this == SUPER_ADMIN;
    }

    public boolean isAdmin() {
        return this == COUNSELING_ADMIN || this == NONCURRICULAR_ADMIN ||
                this == COMPETENCY_ADMIN || this == SUPER_ADMIN;
    }
}
