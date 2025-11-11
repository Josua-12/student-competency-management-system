package com.competency.scms.dto.competency;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//리퀘스트 DTO
@Getter
@Setter
@NoArgsConstructor
public class CompetencyFormDto {

    private Long id;

    private Long parentId;

    private String name;
    private String compCode;
    private String description;
    private int displayOrder;
    private boolean isActive;
    private String adviceHigh;
    private String adviceLow;
}
