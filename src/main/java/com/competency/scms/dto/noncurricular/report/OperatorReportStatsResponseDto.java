package com.competency.scms.dto.noncurricular.report;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorReportStatsResponseDto {
    private Integer apply;
    private Integer attend;
    private Integer complete;
    private Integer fail;
    private Integer survey;
    private Double  rating;
    private Integer mileage;
}

