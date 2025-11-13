package com.competency.scms.dto.noncurricular.report;

import com.competency.scms.domain.noncurricular.report.ReportStatus;
import com.competency.scms.domain.noncurricular.report.ReportType;
import lombok.*;

import java.util.List;

/** 운영자 탭 - 폼 조회 응답 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorReportFormResponseDto {

    private Long programId;
    private Long scheduleId;
    private Long reportId;

    // 기본정보
    private String title;
    private String dept;
    private String period;
    private String runPlace;
    private String contact;

    // 보고서 본문
    private String overview;
    private String resultSummary;
    private String satisfaction;
    private String competency;
    private String issues;
    private String improve;

    // KPI/통계
    private Integer apply;
    private Integer attend;
    private Integer complete;
    private Integer fail;
    private Integer survey;
    private Double  rating;
    private Integer mileage;

    private ReportStatus status;
    private ReportType   reportType;

    private List<BudgetItemDto> budget;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BudgetItemDto {
        private Long id;       // 수정 시 사용(선택)
        private String name;
        private Integer amt;
        private String memo;
    }
}

