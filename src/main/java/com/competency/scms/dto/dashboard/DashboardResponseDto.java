package com.competency.scms.dto.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponseDto {
    private String userName;
    private String userNum;
    private CompetencyChartDto competencyChart;
    private List<ConsultationHistoryDto> recentConsultations;
    private List<RecentProgramDto> recentPrograms;
}