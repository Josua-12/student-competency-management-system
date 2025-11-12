package com.competency.scms.dto.competency;

import com.competency.scms.domain.competency.AssessmentQuestion;
import com.competency.scms.domain.competency.QuestionType;
import lombok.Getter;

@Getter
public class QuestionListDto {

    private Long id;
    private String questionCode;
    private String questionText;
    private QuestionType questionType;
    private int displayOrder;
    private boolean isActive;

    // dto로 변환하는 편의 메서드
    public QuestionListDto(AssessmentQuestion entity) {
        this.id = entity.getId();
        this.questionCode = entity.getQuestionCode();
        this.questionText = entity.getQuestionText();
        this.questionType = entity.getQuestionType();
        this.displayOrder = entity.getDisplayOrder();
        this.isActive = entity.isActive();
    }
}
