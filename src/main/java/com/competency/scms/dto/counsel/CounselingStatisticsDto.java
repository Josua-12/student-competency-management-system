package com.competency.scms.dto.counsel;

import com.competency.scms.domain.counseling.CounselingField;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class CounselingStatisticsDto {
    
    // 상담 유형별 통계
    @Data
    public static class TypeStatistics {
        private CounselingField counselingField;
        private Long totalCount;
        private Long completedCount;
        private Long pendingCount;
        private Long cancelledCount;
        private Double completionRate;
        private Long careerCount;
        private Long academicCount;
        private Long psychologicalCount;
        private Long employmentCount;
    }
    
    // 전체 상담 현황 통계
    @Data
    public static class OverallStatistics {
        private Long totalReservations;
        private Long completedReservations;
        private Long pendingReservations;
        private Long cancelledReservations;
        private Double completionRate;
        private Map<CounselingField, Long> typeDistribution;
        private Long totalCount;
        private Long pendingCount;
        private Long confirmedCount;
        private Long completedCount;
        private Long cancelledCount;
        private Long rejectedCount;
    }
    
    // 상담원별 현황
    @Data
    public static class CounselorStatistics {
        private String counselorName;
        private CounselingField specialization;
        private Long totalAssigned;
        private Long completedCount;
        private Long pendingCount;
        private Double averageSatisfaction;
        private Boolean isActive;
    }
    
    // 상담만족도 결과
    @Data
    public static class SatisfactionResult {
        private String counselorName;
        private Long totalResponses;
        private Double averageRating;
        private Map<Integer, Long> ratingDistribution;
        private LocalDate periodStart;
        private LocalDate periodEnd;
    }
}