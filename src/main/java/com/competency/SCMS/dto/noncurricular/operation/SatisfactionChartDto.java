package com.competency.SCMS.dto.noncurricular.operation;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SatisfactionChartDto {
    private ChartSeriesDto bySchedule;
    private ChartHistogramDto histogram;
}
