package com.competency.scms.dto.noncurricular.mileage;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MileageEligibleRowDto {
    Long applicationId;    // 신청 ID (선택 키)
    Long studentId;        // 학생 userId
    String studentNo;      // 학번
    String name;           // 이름
    String dept;           // 학과
    Integer grade;         // 학년
    String completionStatus; // 이수상태 (COMPLETED/ATTENDED 등)
    Integer existingPoints;  // 기존 포인트(이 프로그램 기준 or 전체 기준)
}

