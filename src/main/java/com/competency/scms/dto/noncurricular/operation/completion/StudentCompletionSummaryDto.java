package com.competency.scms.dto.noncurricular.operation.completion;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentCompletionSummaryDto {

    /** 전체 이수 프로그램 수 */
    private long totalCompletedCount;

    /** 올해 이수 프로그램 수 */
    private long thisYearCompletedCount;

    /** 누적 비교과 포인트 */
    private long totalPoint;

    /** 올해 비교과 포인트 */
    private long thisYearPoint;

    /** 기준 연도 (예: 2025) */
    private String year;
}

