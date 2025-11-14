package com.competency.scms.dto.noncurricular.mypage;

import com.competency.scms.domain.noncurricular.operation.ProgramApplication;
import java.time.LocalDateTime;

public record ApplicationStatusDto(
        Long applicationId,
        String programTitle,
        String status,
        LocalDateTime appliedAt,
        LocalDateTime approvedAt,
        LocalDateTime rejectedAt
) {
    public static ApplicationStatusDto from(ProgramApplication application) {
        return new ApplicationStatusDto(
                application.getApplicationId(),
                application.getProgram().getTitle(),
                application.getStatus().name(),
                application.getAppliedAt(),
                application.getApprovedAt(),
                application.getRejectedAt()
        );
    }
}
