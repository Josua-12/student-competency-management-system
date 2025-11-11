package com.competency.scms.dto.competency;

import lombok.Data;

import java.util.List;

@Data
public class RootCompetencyDto {
    private Long id;
    private String name;
    private String description;

    // 하위 역량 목록
    private List<SubCompetencyDto> subCompetencies;
}
