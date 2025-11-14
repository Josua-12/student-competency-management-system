package com.competency.scms.dto.noncurricular.noncurriDashboard.op;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class OperatorApprovalRequestDto {
    private Long programId;
    private String title;
    private LocalDate requestedAt;
    private String status; // PENDING, REJECTED 등 (문자열 or Enum name)
}

