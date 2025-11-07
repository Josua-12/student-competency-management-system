package com.competency.SCMS.dto.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.operation.SurveyStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SatisfactionSurveyResponse {
    private Long surveyId;
    private Long programId;
    private List<Long> scheduleIds;
    private String title;
    private LocalDateTime openStart;
    private LocalDateTime openEnd;
    private boolean anonymous;
    private boolean requiredToComplete;
    private SurveyStatus status;

    private List<Item> items;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Item {
        private Long questionId;
        private Integer order;
        private String type;
        private String title;
        private boolean required;
        private List<Opt> options;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Opt {
        private Long optionId;
        private Integer order;
        private String text;
    }
}
