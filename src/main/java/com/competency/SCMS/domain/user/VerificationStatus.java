package com.competency.SCMS.domain.user;

public enum VerificationStatus {
    PENDING("인증 대기 중"),
    VERIFIED("인증 완료"),
    EXPIRED("인증 만료"),
    FAILED("인증 실패"),
    BLOCKED("차단됨");

    private final String description;

    VerificationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
