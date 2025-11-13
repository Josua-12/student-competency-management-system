package com.competency.scms.service.noncurricular.report;

import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.noncurricular.program.ProgramSchedule;
import com.competency.scms.domain.noncurricular.report.ProgramReport;
import com.competency.scms.domain.noncurricular.report.ReportStatus;
import com.competency.scms.domain.noncurricular.report.ReportType;
import com.competency.scms.domain.user.User;
import com.competency.scms.dto.noncurricular.report.*;
import com.competency.scms.repository.noncurricular.program.ProgramRepository;
import com.competency.scms.repository.noncurricular.program.ProgramScheduleRepository;
import com.competency.scms.repository.noncurricular.report.ProgramReportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramReportServiceImpl implements ProgramReportService {

    private final ProgramRepository programRepository;
    private final ProgramScheduleRepository scheduleRepository;
    private final ProgramReportRepository reportRepository;

    // (신청/출결/만족도/마일리지 통계용 리포지토리는 필요 시 주입해서 사용하면 됨)
    // private final ProgramApplicationRepository applicationRepository;
    // private final ProgramAttendanceRepository attendanceRepository;
    // private final ProgramSatisfactionRepository satisfactionRepository;
    // private final MileageRecordRepository mileageRecordRepository;

    @Override
    @Transactional(readOnly = true)
    public OperatorReportFormResponseDto loadOperatorForm(Long programId, Long scheduleId, User writer) {

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found: " + programId));

        ProgramSchedule schedule = null;
        if (scheduleId != null) {
            schedule = scheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new EntityNotFoundException("Schedule not found: " + scheduleId));
        }

        ProgramReport report = reportRepository
                .findByProgram_ProgramIdAndSchedule_ScheduleIdAndReportType(
                        programId, scheduleId, ReportType.MANAGER
                )
                .orElse(null);

        OperatorReportStatsResponseDto stats = loadStats(programId, scheduleId);

        OperatorReportFormResponseDto.OperatorReportFormResponseDtoBuilder builder =
                OperatorReportFormResponseDto.builder()
                        .programId(programId)
                        .scheduleId(scheduleId)
                        .title(program.getTitle())
                        .dept(program.getDepartment() != null ? program.getDepartment().getName() : null)
                        .period(formatPeriod(program.getProgramStartAt(), program.getProgramEndAt()))
                        .runPlace(program.isOnline()
                                ? "온라인" + (program.getOnlineUrl() != null ? "(" + program.getOnlineUrl() + ")" : "")
                                : "오프라인" + (program.getLocationText() != null ? "(" + program.getLocationText() + ")" : "")
                        )
                        .contact(buildContact(program))
                        .apply(stats.getApply())
                        .attend(stats.getAttend())
                        .complete(stats.getComplete())
                        .fail(stats.getFail())
                        .survey(stats.getSurvey())
                        .rating(stats.getRating())
                        .mileage(stats.getMileage());

        if (report != null) {
            builder.reportId(report.getReportId())
                    .title(report.getTitle())
                    .dept(report.getDeptName())
                    .period(report.getPeriodText())
                    .runPlace(report.getRunPlace())
                    .contact(report.getContact())
                    .overview(report.getOverview())
                    .resultSummary(report.getResultSummary())
                    .satisfaction(report.getSatisfactionSummary())
                    .competency(report.getCompetencyMapping())
                    .issues(report.getIssues())
                    .improve(report.getImprovement())
                    .status(report.getStatus())
                    .reportType(report.getReportType());
        }

        // 예산 항목은 별도 엔티티로 확장 가능. 지금은 빈 리스트
        builder.budget(List.of());

        return builder.build();
    }

    @Override
    @Transactional(readOnly = true)
    public OperatorReportStatsResponseDto loadStats(Long programId, Long scheduleId) {

        // TODO: 실제 구현 시
        //  - ProgramApplication / ProgramAttendance / ProgramSatisfaction / MileageRecord 등을 이용해 통계 계산
        //  - 지금은 기본 0 / null 값으로 리턴

        int apply = 0;
        int attend = 0;
        int complete = 0;
        int fail = 0;
        int survey = 0;
        Double rating = null;
        int mileage = 0;

        return OperatorReportStatsResponseDto.builder()
                .apply(apply)
                .attend(attend)
                .complete(complete)
                .fail(fail)
                .survey(survey)
                .rating(rating)
                .mileage(mileage)
                .build();
    }

    @Override
    public OperatorReportResponseDto saveOperatorReport(OperatorReportSaveRequestDto req, User writer) {

        Program program = programRepository.findById(req.getProgramId())
                .orElseThrow(() -> new EntityNotFoundException("Program not found: " + req.getProgramId()));

        ProgramSchedule schedule = null;
        if (req.getScheduleId() != null) {
            schedule = scheduleRepository.findById(req.getScheduleId())
                    .orElseThrow(() -> new EntityNotFoundException("Schedule not found: " + req.getScheduleId()));
        }

        ProgramReport report = reportRepository
                .findByProgram_ProgramIdAndSchedule_ScheduleIdAndReportType(
                        req.getProgramId(), req.getScheduleId(), ReportType.MANAGER
                )
                .orElse(ProgramReport.builder()
                        .program(program)
                        .schedule(schedule)
                        .writer(writer)
                        .reportType(ReportType.MANAGER)
                        .status(ReportStatus.DRAFT)
                        .build()
                );

        mapFromOperatorRequest(req, report);
        report.setStatus(ReportStatus.DRAFT);

        ProgramReport saved = reportRepository.save(report);

        return OperatorReportResponseDto.builder()
                .reportId(saved.getReportId())
                .status(saved.getStatus())
                .build();
    }

    @Override
    public OperatorReportResponseDto submitOperatorReport(OperatorReportSaveRequestDto req, User writer) {

        OperatorReportResponseDto saved = saveOperatorReport(req, writer);

        ProgramReport report = reportRepository.findById(saved.getReportId())
                .orElseThrow(() -> new EntityNotFoundException("Report not found: " + saved.getReportId()));

        report.setStatus(ReportStatus.SUBMITTED);

        return OperatorReportResponseDto.builder()
                .reportId(report.getReportId())
                .status(report.getStatus())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentReportListItemDto> findStudentTargets(Long studentId, ReportStatus status) {
        // TODO: ProgramApplication / ProgramAttendance를 사용해서
        //  - "이수(또는 참석) 완료된 프로그램"들 조회 → StudentReportListItemDto로 변환
        //  - 지금은 빈 리스트 반환
        return List.of();
    }

    @Override
    public StudentReportResponseDto saveStudentReport(StudentReportSaveRequestDto req, User student) {

        Program program = programRepository.findById(req.getProgramId())
                .orElseThrow(() -> new EntityNotFoundException("Program not found: " + req.getProgramId()));

        ProgramSchedule schedule = null;
        if (req.getScheduleId() != null) {
            schedule = scheduleRepository.findById(req.getScheduleId())
                    .orElseThrow(() -> new EntityNotFoundException("Schedule not found: " + req.getScheduleId()));
        }

        ProgramReport report = reportRepository
                .findByProgram_ProgramIdAndSchedule_ScheduleIdAndReportType(
                        req.getProgramId(), req.getScheduleId(), ReportType.STUDENT
                )
                .orElse(ProgramReport.builder()
                        .program(program)
                        .schedule(schedule)
                        .writer(student)
                        .reportType(ReportType.STUDENT)
                        .status(ReportStatus.DRAFT)
                        .build()
                );

        report.setStudentGoal(req.getGoal());
        report.setStudentActivity(req.getActivity());
        report.setStudentReflection(req.getReflection());
        report.setStudentPlan(req.getPlan());
        report.setStatus(ReportStatus.DRAFT);

        ProgramReport saved = reportRepository.save(report);

        return StudentReportResponseDto.builder()
                .reportId(saved.getReportId())
                .status(saved.getStatus())
                .build();
    }

    @Override
    public StudentReportResponseDto submitStudentReport(StudentReportSaveRequestDto req, User student) {

        StudentReportResponseDto saved = saveStudentReport(req, student);

        ProgramReport report = reportRepository.findById(saved.getReportId())
                .orElseThrow(() -> new EntityNotFoundException("Report not found: " + saved.getReportId()));

        report.setStatus(ReportStatus.SUBMITTED);

        return StudentReportResponseDto.builder()
                .reportId(report.getReportId())
                .status(report.getStatus())
                .build();
    }

    // ===== 내부 매핑 메서드 =====

    private static void mapFromOperatorRequest(OperatorReportSaveRequestDto req, ProgramReport report) {

        report.setTitle(req.getTitle());
        report.setDeptName(req.getDept());
        report.setPeriodText(req.getPeriod());
        report.setRunPlace(req.getRunPlace());
        report.setContact(req.getContact());

        if (req.getBody() != null) {
            report.setOverview(req.getBody().getOverview());
            report.setResultSummary(req.getBody().getResultSummary());
            report.setSatisfactionSummary(req.getBody().getSatisfaction());
            report.setCompetencyMapping(req.getBody().getCompetency());
            report.setIssues(req.getBody().getIssues());
            report.setImprovement(req.getBody().getImprove());
        }

        if (req.getStats() != null) {
            report.setApplyCount(req.getStats().getApply());
            report.setAttendCount(req.getStats().getAttend());
            report.setCompleteCount(req.getStats().getComplete());
            report.setFailCount(req.getStats().getFail());
        }
    }

    private String formatPeriod(LocalDateTime start, LocalDateTime end) {
        if (start == null && end == null) return "";
        if (start == null) return " ~ " + end.toLocalDate();
        if (end == null) return start.toLocalDate() + " ~ ";
        if (start.toLocalDate().equals(end.toLocalDate())) {
            return start.toLocalDate().toString();
        }
        return start.toLocalDate() + " ~ " + end.toLocalDate();
    }

    private String buildContact(Program program) {
        StringBuilder sb = new StringBuilder();
        if (program.getOwnerName() != null && !program.getOwnerName().isBlank()) {
            sb.append(program.getOwnerName());
        }
        if (program.getOwnerTel() != null && !program.getOwnerTel().isBlank()) {
            if (sb.length() > 0) sb.append(" / ");
            sb.append(program.getOwnerTel());
        }
        if (program.getOwnerEmail() != null && !program.getOwnerEmail().isBlank()) {
            if (sb.length() > 0) sb.append(" / ");
            sb.append(program.getOwnerEmail());
        }
        return sb.toString();
    }
}

