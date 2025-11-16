package com.competency.scms.dto.competency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResultParentCompetencyDto {
    private String name;    // "자기관리 역량"
    // 부모역량 평균 점수
    private double parentAverageScore;
    private List<ResultChildCompetencyDto> children;
}
