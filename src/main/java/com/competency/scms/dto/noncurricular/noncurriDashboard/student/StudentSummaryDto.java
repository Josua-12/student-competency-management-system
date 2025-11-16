package com.competency.scms.dto.noncurricular.noncurriDashboard.student;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentSummaryDto {
    private long totalApplied;     // 총 신청
    private long active;           // 참여중
    private long completed;        // 이수 완료
    private long mileage;          // 누적 포인트
}
