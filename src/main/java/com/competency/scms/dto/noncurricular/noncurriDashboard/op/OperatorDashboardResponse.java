package com.competency.scms.dto.noncurricular.noncurriDashboard.op;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OperatorDashboardResponse {

    private LocalDateTime lastUpdated;

    // KPI
    private long pendingApproval;      // 승인 대기 프로그램 수
    private long todayAttendance;      // 금일 출석 관리 대상 수
    private long pendingReports;       // 결과보고서 미검토 수
    private long pendingCompletion;    // 이수처리 대기 수

    // 그래프
    private List<OperatorMonthlyProgramStatDto> monthlyPrograms;
    private List<OperatorCategoryStatDto> topCategories;

    // 목록
    private List<OperatorApprovalRequestDto> approvalRequests;
    private List<OperatorPendingTaskDto> pendingTasks;
}

