package com.competency.SCMS.domain.noncurricular.operation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SurveyStatus {
    DRAFT("초안"),
    PUBLISHED("게시");

    private final String label;
}
