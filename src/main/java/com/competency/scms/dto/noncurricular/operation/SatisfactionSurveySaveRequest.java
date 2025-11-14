package com.competency.scms.dto.noncurricular.operation;

import com.competency.scms.domain.noncurricular.operation.QuestionType;
import com.competency.scms.domain.noncurricular.operation.SurveyStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SatisfactionSurveySaveRequest {
    @NotNull
    private Long programId;

    // '전체회차'는 빈 배열로 보냄
    @Builder.Default
    private List<Long> scheduleIds = List.of();

    @NotBlank
    private String title;

    private LocalDateTime openStart;
    private LocalDateTime openEnd;

    @Builder.Default
    private boolean anonymous = true;

    @Builder.Default
    private boolean requiredToComplete = true;

    @NotNull
    private SurveyStatus status;    // DRAFT | PUBLISHED

    @NotNull
    @Size(min = 1, message = "문항은 1개 이상이어야 합니다.")
    private List<QuestionDto> questions;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class QuestionDto {
        @NotBlank
        private String id;       // 프론트 UID(신규 저장 시 서버에서 별도 PK 생성)
        @NotNull
        private Integer order;
        @NotNull
        private QuestionType type;
        @NotBlank
        private String title;
        @Builder.Default
        private boolean required = true;
        @Builder.Default
        private List<OptionDto> options = List.of(); // rating/short/long은 비어있음
        private Integer scale; // rating일 때 (기본 5)
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OptionDto {
        @NotBlank
        private String id; // 프론트 UID
        @NotNull
        private Integer order;
        @NotBlank
        private String text;
    }
}
