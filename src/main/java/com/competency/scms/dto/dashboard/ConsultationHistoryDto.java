package com.competency.scms.dto.dashboard;

import java.time.LocalDateTime;

public record ConsultationHistoryDto(
        Long id,
        String counselorName,
        LocalDateTime reservationDate,
        String status,
        String type
) {
    public static ConsultationHistoryDto of(Long id, String counselorName, 
                                          LocalDateTime reservationDate, String status, String type) {
        return new ConsultationHistoryDto(id, counselorName, reservationDate, status, type);
    }
}