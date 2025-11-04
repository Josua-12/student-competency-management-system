package com.competency.SCMS.domain.counseling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CounselingField {
    PSYCHOLOGICAL("심리상담"),
    CAREER("진로상담"),
    EMPLOYMENT("취업상담"),
    ACADEMIC("학습상담");

    private final String displayName;

}
