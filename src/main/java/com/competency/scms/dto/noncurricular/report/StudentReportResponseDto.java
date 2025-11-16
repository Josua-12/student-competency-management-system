package com.competency.scms.dto.noncurricular.report;

import com.competency.scms.domain.noncurricular.report.ReportStatus;
import lombok.*;

/** 학생 탭 - 응답 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentReportResponseDto {
    private Long reportId;
    private ReportStatus status;
}
