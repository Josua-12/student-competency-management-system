package com.competency.SCMS.domain.user;

public enum UserRole {
    STUDENT("학생"),
    CONSELOR("상담사"),
    ADMIN("관리자"),
    OPERATOR("운영자");

    private final String descrition;

    UserRole(String descrition) {
        this.descrition = descrition;
    }

    public String getDescrition() {
        return descrition;
    }
}
