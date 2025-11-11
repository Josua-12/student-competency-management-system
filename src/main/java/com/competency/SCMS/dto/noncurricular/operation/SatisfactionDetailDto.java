package com.competency.SCMS.dto.noncurricular.operation;

import lombok.*;

import java.time.LocalDateTime;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SatisfactionDetailDto {
    private Long id;
    private Long programId;
    private String programTitle;
    private Long scheduleId;
    private String scheduleName;
    private String studentNoMasked;
    private String studentNameMasked;
    private Integer rating;
    private String feedback;
    private LocalDateTime submittedAt;
}
