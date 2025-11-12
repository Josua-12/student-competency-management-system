package com.competency.SCMS.dto.noncurricular.operation;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChartSeriesDto {
    private List<String> labels;  // 차트 X축 레이블
    private List<Double> values;  // 차트 값 (평균 점수 등)
}
