package com.competency.scms.dto.noncurricular.operation.application;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentApplicationListDto {
    private Long applicationId;
    private String programName;
    private String categoryName;
    private String period;            // 2025-03-10 ~ 2025-03-12
    private String appliedDate;       // 신청일
    private String status;            // 승인/대기/반려/취소
    private Integer point;            // 비교과 포인트
    private String completionStatus;  // 이수 여부
    private String schedule;          // 활동 일정
}

