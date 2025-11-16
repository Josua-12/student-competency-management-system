package com.competency.scms.dto.noncurricular.noncurriDashboard.op;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OperatorCategoryStatDto {
    private String categoryName; // 예: "진로·취업"
    private long count;          // 개설/참여 횟수
    private long participantCount; // 참여자 수
    
    public OperatorCategoryStatDto(Object category, long count, long participantCount) {
        this.categoryName = category != null ? category.toString() : "Unknown";
        this.count = count;
        this.participantCount = participantCount;
    }
}
