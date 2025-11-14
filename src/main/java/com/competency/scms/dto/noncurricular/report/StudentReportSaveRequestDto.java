package com.competency.scms.dto.noncurricular.report;

import lombok.*;

/** 학생 탭 - 저장/제출 요청 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentReportSaveRequestDto {

    private Long applicationId;
    private Long programId;
    private Long scheduleId;

    private String goal;
    private String activity;
    private String reflection;
    private String plan;
}

