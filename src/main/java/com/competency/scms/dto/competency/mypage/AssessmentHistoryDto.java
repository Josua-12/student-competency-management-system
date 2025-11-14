package com.competency.scms.dto.competency.mypage;

import com.competency.scms.domain.competency.AssessmentResult;
import java.time.LocalDateTime;

public record AssessmentHistoryDto(
        Long resultId,
        String sectionTitle,
        LocalDateTime submittedAt,
        String status
) {
    public static AssessmentHistoryDto from(AssessmentResult result) {
        return new AssessmentHistoryDto(
                result.getId(),
                result.getAssessmentSection().getTitle(),
                result.getSubmittedAt(),
                result.getStatus().name()
        );
    }
}
