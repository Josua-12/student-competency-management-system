package com.competency.scms.domain.counseling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprovalStatus {
    PENDING("승인대기"),
    APPROVED("승인"),
    REJECTED("반려"),
    CANCELLED("취소됨");

    private final String displayName;

}
