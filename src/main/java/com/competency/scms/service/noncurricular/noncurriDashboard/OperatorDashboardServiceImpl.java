package com.competency.scms.service.noncurricular.noncurriDashboard;

import com.competency.scms.domain.noncurricular.operation.AttendanceStatus;
import com.competency.scms.domain.noncurricular.program.ProgramStatus;
import com.competency.scms.domain.noncurricular.report.ReportStatus;
import com.competency.scms.dto.noncurricular.noncurriDashboard.op.OperatorDashboardResponse;
import com.competency.scms.dto.noncurricular.noncurriDashboard.op.OperatorPendingTaskDto;
import com.competency.scms.repository.noncurricular.mileage.MileageRecordRepository;
import com.competency.scms.repository.noncurricular.operation.ProgramApplicationRepository;
import com.competency.scms.repository.noncurricular.operation.ProgramAttendanceRepository;
import com.competency.scms.repository.noncurricular.program.ProgramRepository;
import com.competency.scms.repository.noncurricular.report.ProgramReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperatorDashboardServiceImpl implements OperatorDashboardService {

    private final ProgramRepository programRepository;
    private final ProgramApplicationRepository applicationRepository;
    private final ProgramAttendanceRepository attendanceRepository;
    private final ProgramReportRepository reportRepository;

    @Override
    public OperatorDashboardResponse getDashboard() {

        // KPI
        long pendingApproval = programRepository.countByStatus(ProgramStatus.PENDING);
        long todayAttendance = attendanceRepository.countByAttendDate(LocalDate.now());
        long pendingReports   = reportRepository.countByStatus(ReportStatus.SUBMITTED);
        long pendingCompletion = attendanceRepository.countByStatus(AttendanceStatus.PRESENT);

        // 최근 6개월 기준
        var fromDate = LocalDate.now().minus(6, ChronoUnit.MONTHS);
        var monthlyPrograms = programRepository.findMonthlyProgramStats(fromDate);

        // 카테고리 top5
        var topCategories = programRepository.findTopCategoryStats(PageRequest.of(0, 5));

        // 승인 요청 최근 5건
        var approvalRequests = applicationRepository.findPendingApprovalRequests(PageRequest.of(0, 5));

        // 미처리 업무는 여러 소스에서 조합해야 하는데,
        // 여기서는 일단 샘플로 빈 리스트 또는 후에 비즈니스 로직에 맞게 구성
        List<OperatorPendingTaskDto> pendingTasks = List.of(
                // TODO: 실제 비즈니스 규칙에 맞게 조합
        );

        return OperatorDashboardResponse.builder()
                .lastUpdated(LocalDateTime.now())
                .pendingApproval(pendingApproval)
                .todayAttendance(todayAttendance)
                .pendingReports(pendingReports)
                .pendingCompletion(pendingCompletion)
                .monthlyPrograms(monthlyPrograms)
                .topCategories(topCategories)
                .approvalRequests(approvalRequests)
                .pendingTasks(pendingTasks)
                .build();
    }
}

