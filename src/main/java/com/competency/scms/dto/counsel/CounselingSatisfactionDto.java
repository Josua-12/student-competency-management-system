package com.competency.scms.dto.counsel;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;
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

    // 제출된 만족도 조회용
    @Data
    public static class ResultResponse {
        private Long satisfactionId;
        private Long reservationId;
        private String counselorName;
        private LocalDateTime submittedAt;
        private List<AnswerResponse> answers;
        
        @Data
        public static class AnswerResponse {
            private Long questionId;
            private String questionText;
            private String questionType;
            private String answerText;
            private Integer ratingValue;
            private Long selectedOptionId;
            private String selectedOptionText;
        }
    }
    
    // 상담사 만족도 요약
    @Data
    public static class SummaryResponse {
        private Double avgSatisfaction;
        private Double responseRate;
        private Long totalResponses;
        private Double reusageRate;
    }
    
    // 상담사 만족도 분포
    @Data
    public static class DistributionResponse {
        private Long score5Count;
        private Long score4Count;
        private Long score3Count;
        private Long score2Count;
        private Long score1Count;
        private Double score5Percent;
        private Double score4Percent;
        private Double score3Percent;
        private Double score2Percent;
        private Double score1Percent;
    }
    
    // 상담사 월별 만족도 추이
    @Data
    public static class MonthlyResponse {
        private java.util.List<String> months;
        private java.util.List<Double> avgSatisfactions;
    }
}