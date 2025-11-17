package com.competency.scms.dto.noncurricular.operation.completion;


import com.competency.scms.domain.noncurricular.program.CompletionStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class OpCompletionStatusSearchConditionDto {
    // 기간 구분: PROGRAM_END, RUNNING, APPLY
    private String dateType;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    // 프로그램명, 프로그램 ID
    private String programName;
    private String progId;

    // 운영부서 (dept 코드 or id)
    private Long deptId;

    // 이수상태
    private CompletionStatus completionStatus;

    // 출석률 하한 (예: 80, 90)
    private Integer attendanceMin;

    // 만족도 설문 제출 여부: "Y", "N", null
    private String satisfactionSubmitted;
}
