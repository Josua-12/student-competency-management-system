package com.competency.scms.dto.competency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetencyScoreDto {
    private String competencyName;
    private Double score;
}
