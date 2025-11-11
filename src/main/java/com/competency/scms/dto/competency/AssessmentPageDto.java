package com.competency.scms.dto.competency;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AssessmentPageDto {

    private Long resultId;
    private String assessmentTitle;
    private List<RootCompetencyDto> rootCompetencies;

    // 사용자가 임시저장한 응답
    // Key: Question ID, Value: Option ID
    private Map<Long, Long> savedResponses;
}
