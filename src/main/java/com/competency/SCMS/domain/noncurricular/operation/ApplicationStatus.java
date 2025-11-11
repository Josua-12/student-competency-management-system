package com.competency.SCMS.domain.noncurricular.operation;

public enum ApplicationStatus {
    PENDING,       // 승인요청(대기)
    APPROVED,      // 승인
    REJECTED,      // 반려
    CANCELED,      // 취소(학생/운영자)
    WAITLISTED     // 대기열
}
