package com.competency.scms.dto.competency;

import com.competency.scms.domain.competency.QuestionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

//리퀘스트 dto
@Getter
@Setter
@NoArgsConstructor
public class QuestionFormDto {

    // 문항 ID
    private Long id;

    // 문항이 속한 역량 ID
    private Long competencyId;

    // --- 폼 필드 ---
    private String questionText;
    private String questionCode;
    private QuestionType questionType;
    private int displayOrder;
    private boolean isActive;

    // --- 보기 목록 ---
    private List<OptionFormDto> options = new ArrayList<>();
}
