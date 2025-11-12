package com.competency.SCMS.domain.user;

public enum VerificationStatus {
    PENDING("대기중"),
    VERIFIED("인증완료"),
    EXPIRED("만료됨"),
    BLOCKED("차단됨"),
    USED("사용중");

    private final String description;

    VerificationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
