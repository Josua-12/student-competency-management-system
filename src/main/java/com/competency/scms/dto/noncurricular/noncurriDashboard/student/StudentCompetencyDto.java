package com.competency.scms.dto.noncurricular.noncurriDashboard.student;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudentCompetencyDto {

    private String lastAssessmentDate; // "2025-11-01" 문자열 포맷
    private List<StudentCompetencyScoreDto> scores;
}
