package com.competency.scms.dto.noncurricular.operation;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SatisfactionPageResponseDto {
    private List<SatisfactionListItemDto> content;
    private long totalElements;
    private Double avgRating;     // KPI
    private Long count;           // 응답 수
    private Double responseRate;  // 응답률(응답수/참여자수)
    private Double posRatio;      // 4~5 비율
    private Double negRatio;      // 1~2 비율
    private SatisfactionChartDto chart;
}
