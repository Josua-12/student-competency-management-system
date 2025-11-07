package com.competency.SCMS.dto.noncurricular.operation;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SatisfactionListItemDto {
    private Long id;
    private Long programId;
    private String programTitle;
    private Long scheduleId;
    private String scheduleName;
    private String studentNoMasked;   // 화면용 마스킹
    private String studentNameMasked; // 옵션(익명화 시 사용)
    private Integer rating;           // 1~5
    private String feedback;          // 일부 표시(말줄임은 프런트 처리)
    private LocalDateTime submittedAt;
}