package com.competency.scms.domain.noncurricular.program;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RunType {

    OFFLINE("오프라인"),
    ONLINE("온라인"),
    HYBRID("혼합");

    private final String label;
}
