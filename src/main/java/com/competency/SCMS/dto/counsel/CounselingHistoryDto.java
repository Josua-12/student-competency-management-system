package com.competency.SCMS.dto.counsel;

import com.competency.SCMS.domain.counseling.CounselingField;
import com.competency.SCMS.domain.counseling.ReservationStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CounselingHistoryDto {
    
    // 상담 이력 조회용
    @Data
    public static class HistoryResponse {
        private Long id;
        private String studentName;
        private String studentId;
        private String counselorName;
        private CounselingField counselingField;
        private String subFieldName;
        private LocalDate counselingDate;
        private ReservationStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
    }
    
    // 상담 현황 조회용
    @Data
    public static class StatusResponse {
        private String counselorName;
        private Long totalCount;
        private Long completedCount;
        private Long pendingCount;
        private Long cancelledCount;
    }
    
    // 검색 조건
    @Data
    public static class SearchCondition {
        private String studentName;
        private String studentId;
        private String counselorName;
        private CounselingField counselingField;
        private ReservationStatus status;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer page = 0;
        private Integer size = 10;
    }
}