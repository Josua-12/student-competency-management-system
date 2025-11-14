package com.competency.scms.dto.noncurricular.operation;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ParticipantSearchConditionDto {
    String status;      // "", "PENDING" | "APPROVED" | "REJECTED" | "CANCELLED"
    Long scheduleId;    // null 허용
    String q;           // 키워드 (이름/학번/학과/연락처)
}
