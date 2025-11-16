package com.competency.scms.dto.noncurricular.report;

import com.competency.scms.domain.noncurricular.report.ReportStatus;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentReportListItemDto {
    private Long applicationId;
    private Long programId;
    private Long scheduleId;

    private String programTitle;
    private String sessionName;    // 회차명
    private String activityDate;   // 활동일자(문자열)
    private String attendStatus;   // 출결
    private String completeStatus; // 이수 여부

    private ReportStatus reportStatus;
}
