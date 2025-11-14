package com.competency.scms.dto.competency;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//리퀘스트 dto
@Getter
@Setter
@NoArgsConstructor
public class OptionFormDto {

    private Long id;

    private String optionText;
    private Integer score;
    private int displayOrder;
}
