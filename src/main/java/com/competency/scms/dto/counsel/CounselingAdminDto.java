package com.competency.scms.dto.counsel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CounselingAdminDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardResponse {
        private long totalReservations;
        private long pendingApprovals;
        private long activeCounselors;
        private double avgSatisfaction;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CounselorOption {
        private Long id;
        private String name;
        private String field;
    }
}