package com.competency.scms.dto.noncurricular.report;

import com.competency.scms.domain.noncurricular.report.ReportStatus;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorReportResponseDto {
    private Long reportId;
    private ReportStatus status;
}

