package com.competency.scms.domain.noncurricular.operation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprovalStatus {

    /** 승인요청됨 (운영자가 상신한 상태) */
    REQ("승인요청"),

    /** 검토 중 (부서관리자 또는 시스템관리자 승인 대기 상태) */
    WAIT("검토중"),

    /** 승인 완료 (프로그램 운영 가능) */
    DONE("승인완료"),

    /** 반려 (승인 거절됨) */
    REJ("반려");

    private final String label;

    public static String getLabel(ApprovalStatus status) {
        return status != null ? status.getLabel() : "-";
    }
}
