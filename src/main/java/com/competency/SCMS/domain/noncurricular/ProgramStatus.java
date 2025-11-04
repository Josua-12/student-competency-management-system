package com.competency.SCMS.domain.noncurricular;

public enum ProgramStatus {
    DRAFT,        // 임시저장
    PENDING,      // 승인요청
    APPROVED,     // 승인
    REJECTED,     // 반려
    PUBLISHED,    // 게시(모집/운영중)
    CLOSED,       // 종료
    CANCELED      // 취소
}

