package com.competency.scms.dto.noncurricular.mileage;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MileageEligibleSearchConditionDto {
    Long programId;   // progId (필수)
    Long scheduleId;  // schdId (선택)
    String keyword;   // 이름/학번 검색
    String from;      // 운영기간 from (yyyy-MM-dd)
    String to;        // 운영기간 to
    String deptCode;  // 부서 코드
}

