package com.competency.scms.dto.counsel;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CounselingSatisfactionDto {
    
    // 만족도 제출용
    @Data
    public static class SubmitRequest {
        @NotNull(message = "예약 ID는 필수입니다")
        private Long reservationId;
        @NotEmpty(message = "답변은 필수입니다")
        @Valid
        private List<AnswerRequest> answers;
        
        @Data
        public static class AnswerRequest {
            @NotNull(message = "문항 ID는 필수입니다")
            private Long questionId;
            private String answerText;
            private Integer ratingValue;
            private Long selectedOptionId;
        }
    }
    
    // 만족도 설문 조회용
    @Data
    public static class SurveyResponse {
        private Long reservationId;
        private String counselorName;
        private List<QuestionResponse> questions;
        
        @Data
        public static class QuestionResponse {
            private Long questionId;
            private String questionText;
            private String questionType;  // "RATING", "TEXT", "MULTIPLE_CHOICE"
            private Boolean isRequired;
            private List<OptionResponse> options;
            
            @Data
            public static class OptionResponse {
                private Long optionId;
                private String optionText;
                private Integer optionValue;
            }
        }
    }
}