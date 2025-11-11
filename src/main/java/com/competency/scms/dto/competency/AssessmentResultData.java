package com.competency.scms.dto.competency;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssessmentResultData {
    // 1. 헤더용
    private String assessmentTitle;
    private String userName;
    private LocalDateTime submittedAt;

    // 2. 레이더 차트용
    private RadarChartData radarChartData;

    // 3. 상세 점수 막대 바용
    private List<ResultParentCompetencyDto> scoreDetails;

    // 4. 피드백용
    private List<ResultFeedbackDto> strengths;
    private List<ResultFeedbackDto> weaknesses;

    // 5. 종합 조언 (미정)
    private String overallAdvice;
}
