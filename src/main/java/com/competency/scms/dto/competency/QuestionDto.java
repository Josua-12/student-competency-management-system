package com.competency.scms.dto.competency;

import com.competency.scms.domain.competency.QuestionType;
import lombok.Data;

import java.util.List;

@Data
public class QuestionDto {
    private Long id;
    private String questionText;
    private QuestionType questionType;
    private List<OptionDto> options;    // 문항에 속한 보기 목록
}

