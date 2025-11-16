package com.competency.scms.dto.competency;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompetencyAverageDto {
    private Long competencyId;  // 핵심 역량 ID
    private Double averageScore;    // 계산된 평균 점수
}
