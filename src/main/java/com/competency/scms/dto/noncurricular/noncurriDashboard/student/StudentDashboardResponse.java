package com.competency.scms.dto.noncurricular.noncurriDashboard.student;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class StudentDashboardResponse {

    private LocalDateTime lastUpdated;

    private StudentSummaryDto summary;

    private List<StudentLatestApplicationDto> latestApplications; // 최근 신청 3건
    private List<StudentRecommendationDto> recommendations;       // 추천 프로그램

    private StudentCompetencyDto competency;                      // 역량 진단 정보
}

