package com.competency.scms.dto.noncurricular.noncurriDashboard.student;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentRecommendationDto {
    private Long programId;
    private String title;
    private String category;
    private String periodText;
    private String reason; // "진로탐색 역량 보완 추천" 등
}
