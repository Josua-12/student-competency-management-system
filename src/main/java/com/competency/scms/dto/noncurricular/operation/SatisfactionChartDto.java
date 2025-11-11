package com.competency.scms.dto.noncurricular.operation;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SatisfactionChartDto {
    private ChartSeriesDto bySchedule;
    private ChartHistogramDto histogram;
}
