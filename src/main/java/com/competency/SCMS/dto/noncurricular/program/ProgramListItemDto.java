package com.competency.SCMS.dto.noncurricular.program;

import lombok.Builder;
import lombok.Value;

@Value @Builder
public class ProgramListItemDto {
    Long id;
    String title;
    String deptName;
    String categoryName;
    String status;       // 문자열로 노출
    Integer mileage;
    String periodText;   // "yyyy-MM-dd ~ yyyy-MM-dd"
    String thumbnailUrl;
}