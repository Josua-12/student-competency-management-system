package com.competency.scms.service.noncurricular.report;

import com.competency.scms.domain.user.User;
import com.competency.scms.dto.noncurricular.report.*;
import com.competency.scms.domain.noncurricular.report.ReportStatus;

import java.util.List;

public interface ProgramReportService {

    /** 운영자 탭: 폼 + 기존 저장 내용 */
    OperatorReportFormResponseDto loadOperatorForm(Long programId, Long scheduleId, User writer);

    /** 운영자 탭: KPI / 통계 */
    OperatorReportStatsResponseDto loadStats(Long programId, Long scheduleId);

    /** 운영자 탭: 임시저장 */
    OperatorReportResponseDto saveOperatorReport(OperatorReportSaveRequestDto req, User writer);

    /** 운영자 탭: 제출(승인요청) */
    OperatorReportResponseDto submitOperatorReport(OperatorReportSaveRequestDto req, User writer);

    /** 학생 탭: 나의 결과보고서 대상 목록 */
    List<StudentReportListItemDto> findStudentTargets(Long studentId, ReportStatus status);

    /** 학생 탭: 임시저장 */
    StudentReportResponseDto saveStudentReport(StudentReportSaveRequestDto req, User student);

    /** 학생 탭: 제출 */
    StudentReportResponseDto submitStudentReport(StudentReportSaveRequestDto req, User student);
}

