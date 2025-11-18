package com.competency.scms.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuperAdminDashboardDto {
    private long totalUsers;
    private long totalCompetencyAssessments;
    private long totalCounselors;
    private long activeCounselors;
    private long programCount;
    private long totalMileage;
}