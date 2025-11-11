package com.competency.SCMS.dto.competency;

import lombok.Data;

import java.util.List;

@Data
public class SubCompetencyDto {

    private Long id;
    private String name;
    private String description;
    private List<QuestionDto> questions;

}
