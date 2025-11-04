package com.competency.SCMS.domain.user;

public enum UserRole {
    STUDENT("학생"),
    COUNSELOR("상담사"),
    ADMIN("관리자"),
    OPERATOR("운영자");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}