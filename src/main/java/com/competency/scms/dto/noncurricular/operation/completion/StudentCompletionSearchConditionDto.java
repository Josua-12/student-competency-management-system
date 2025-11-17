package com.competency.scms.dto.noncurricular.operation.completion;

import com.competency.scms.domain.noncurricular.mileage.MileageFilterType;
import com.competency.scms.domain.noncurricular.program.CompletionStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class StudentCompletionSearchConditionDto {

    /** 이수기간 - 시작일 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    /** 이수기간 - 종료일 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;

    /** 프로그램명 (LIKE 검색) */
    private String programName;

    /** 운영부서 ID */
    private Long departmentId;

    /** 이수상태 (COMPLETED / IN_PROGRESS / NOT_COMPLETED) */
    private CompletionStatus completionStatus;

    /** 포인트 구분 (HAS_POINT / NO_POINT) */
    private MileageFilterType pointType = MileageFilterType.ALL;

    /** 핵심역량 ID (단일 선택 버전) */
    private Long competencyId;
}
