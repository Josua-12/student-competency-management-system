package com.competency.scms.domain.noncurricular.operation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SatisfactionStatus {

    NOT_TARGET("대상 아님"),
    NOT_SUBMITTED("미제출"),
    SUBMITTED("제출완료");

    private final String label;
}

