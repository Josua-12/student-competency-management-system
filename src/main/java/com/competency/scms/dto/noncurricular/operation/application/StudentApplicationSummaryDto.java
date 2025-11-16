package com.competency.scms.dto.noncurricular.operation.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data   // getter/setter, toString 등 자동 생성
@NoArgsConstructor
@AllArgsConstructor
public class StudentApplicationSummaryDto {

    private long totalCount;     // 전체 신청 건수
    private long approvedCount;  // 승인
    private long pendingCount;   // 대기
    private long rejectedCount;  // 반려
    private long canceledCount;  // 취소
}


