package com.competency.SCMS.dto.competency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResultParentCompetencyDto {
    private String name;    // "자기관리 역량"
    private List<ResultChildCompetencyDto> children;
}
