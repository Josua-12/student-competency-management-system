package com.competency.scms.dto.noncurricular.noncurriDashboard.student;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentCompetencyScoreDto {
    private String name;   // "진로설계"
    private int score;     // 0~100 or 0~5
}
