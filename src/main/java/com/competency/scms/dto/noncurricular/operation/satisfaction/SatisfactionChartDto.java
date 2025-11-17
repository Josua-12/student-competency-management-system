package com.competency.scms.dto.noncurricular.operation.satisfaction;

import com.competency.scms.dto.noncurricular.operation.ChartHistogramDto;
import com.competency.scms.dto.noncurricular.operation.ChartSeriesDto;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SatisfactionChartDto {
    private ChartSeriesDto bySchedule;
    private ChartHistogramDto histogram;
}
