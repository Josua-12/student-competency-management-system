package com.competency.scms.dto.noncurricular.report;

import lombok.*;

import java.util.List;

/** 운영자 탭 - 저장/제출 요청 DTO (JS collectPayload 구조와 1:1 매핑) */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorReportSaveRequestDto {

    private Long programId;
    private Long scheduleId;
    private Long reportId;  // 수정 시 프론트에서 포함할 수 있음

    private String title;
    private String dept;
    private String period;
    private String runPlace;
    private String contact;

    private Body  body;
    private Stats stats;
    private List<BudgetItem> budget;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Body {
        private String overview;
        private String resultSummary;
        private String satisfaction;
        private String competency;
        private String issues;
        private String improve;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Stats {
        private Integer apply;
        private Integer attend;
        private Integer complete;
        private Integer fail;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BudgetItem {
        private Long id;
        private String name;
        private Integer amt;
        private String memo;
    }
}

