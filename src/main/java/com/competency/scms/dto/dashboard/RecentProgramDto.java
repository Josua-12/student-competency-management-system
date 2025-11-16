package com.competency.scms.dto.dashboard;

import java.time.LocalDateTime;

public record RecentProgramDto(
        Long id,
        String title,
        String category,
        LocalDateTime applicationDeadline,
        String status,
        Integer currentParticipants,
        Integer maxParticipants
) {
    public static RecentProgramDto of(Long id, String title, String category,
                                    LocalDateTime applicationDeadline, String status,
                                    Integer currentParticipants, Integer maxParticipants) {
        return new RecentProgramDto(id, title, category, applicationDeadline, 
                                  status, currentParticipants, maxParticipants);
    }
}