package com.competency.SCMS.dto.noncurricular.linkCompetency;

import lombok.Builder;
import lombok.Value;

@Value @Builder
public class CompetencyDto {
    Long id;
    String name;    // 예: 문제해결, 협업, 의사소통
}
