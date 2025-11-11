package com.competency.scms.dto.noncurricular.operation;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChartHistogramDto {
    private List<Integer> labels; // [1,2,3,4,5]
    private List<Long> values;    // 각 평점 빈도
}
