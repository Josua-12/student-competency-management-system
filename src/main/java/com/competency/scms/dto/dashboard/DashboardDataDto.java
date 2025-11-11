package com.competency.SCMS.dto.dashboard;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDataDto {

    private UserSummary userSummary;
    private ProgramSummary programSummary;
    private CompetencySummary competencySummary;
    private List<RecentActivity> recentActivities;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserSummary {
        private String userName;
        private String email;
        private String role;
        private Integer totalMileage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProgramSummary {
        private Integer enrolledProgramsCount;
        private Integer completedProgramsCount;
        private Integer upcomingProgramsCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompetencySummary {
        private Double averageScore;
        private Integer assessmentCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentActivity {
        private String activityType;
        private String description;
        private String timestamp;
    }
}
