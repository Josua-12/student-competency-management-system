package com.competency.scms.dto.noncurricular.noncurriDashboard.op;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OperatorMonthlyProgramStatDto {
    private String monthLabel;   // "2025-11" or "11월"
    private long programCount;
    private long participantCount;
}

