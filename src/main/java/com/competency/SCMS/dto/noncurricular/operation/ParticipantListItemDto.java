package com.competency.SCMS.dto.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.operation.ApplicationStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ParticipantListItemDto {
    Long applicationId;
    String name;        // 학생명
    Integer studentNo;   // 학번
    String dept;        // 학과
    Integer grade;      // 학년
    String phone;       // 연락처
    String appType;     // 신청유형 (GENERAL/WAITLIST/PRIORITY) → 화면에는 한글로 렌더
    Long scheduleId;    // 회차 ID
    String scheduleName;// 회차명 (예: 1회차(2025-11-10 10:00))
    ApplicationStatus status;
    Integer no;         // 화면 No(선택) - 서버에서 계산 or 프런트에서 계산
}
