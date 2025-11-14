package com.competency.scms.dto.noncurricular.noncurriDashboard.student;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentLatestApplicationDto {
    private Long programId;
    private String title;
    private String periodText; // "2025-11-20 ~ 2025-11-22"
    private String status;     // PENDING, APPROVED, COMPLETED...
    
    public StudentLatestApplicationDto(Long programId, String title, String periodText, Object status) {
        this.programId = programId;
        this.title = title;
        this.periodText = periodText;
        this.status = status != null ? status.toString() : null;
    }
}

