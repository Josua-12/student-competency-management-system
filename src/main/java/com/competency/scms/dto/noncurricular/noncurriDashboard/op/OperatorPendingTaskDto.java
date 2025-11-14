package com.competency.scms.dto.noncurricular.noncurriDashboard.op;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class OperatorPendingTaskDto {
    private String type;    // "출석", "성과", "결과보고", "이수"
    private Long programId;
    private String title;
    private LocalDate dueDate;
    private String status; // "미처리", "미등록", "미검토" 등
}

