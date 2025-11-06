package com.competency.SCMS.dto.noncurricular.operation;

import lombok.Builder;
import lombok.Value;

@Value @Builder
public class ApprovalHistoryDto {
    String requestedAt;  // yyyy-MM-dd HH:mm
    String status;       // REQ/WAIT/DONE/REJ 등
    String actor;        // 처리자명(역할)
    String comment;
}
