package com.competency.scms.dto.noncurricular.program;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProgramBasicDto {
    Long id;
    String title;
    String deptName;
    String categoryName;
    String status;          // DRAFT/PENDING/APPROVED/REJECTED/...
    Integer mileage;        // null 가능
    String appPeriod;      // "yyyy-MM-dd ~ yyyy-MM-dd"
    String runPeriod;
    String location;
    String desc;
    String thumbnailUrl;    // 없으면 null
}