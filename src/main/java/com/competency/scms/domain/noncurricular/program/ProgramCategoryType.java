package com.competency.scms.domain.noncurricular.program;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProgramCategoryType {

    CAREER("진로/취업"),
    GLOBAL("국제/글로벌"),
    LEAD("리더십"),
    VOL("봉사"),
    COUNSEL("상담"),
    MENTOR("멘토링"),
    ACADEMIC("학업/성취"),
    LEADERSHIP("리더십역량"),
    CULTURE("문화/예술");

    private final String label;
}

