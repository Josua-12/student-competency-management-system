package com.competency.scms.repository.noncurricular.report;

import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.noncurricular.report.ProgramReport;
import com.competency.scms.domain.noncurricular.report.ReportStatus;
import com.competency.scms.domain.noncurricular.report.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProgramReportRepository
            extends JpaRepository<ProgramReport, Long>, JpaSpecificationExecutor<ProgramReport> {

        @Query("""
        select r
          from ProgramReport r
         where r.program.programId = :programId
           and r.status = :status
         order by r.createdAt desc
    """)
        List<ProgramReport> findAllByProgramAndStatus(Program program, ReportStatus status);

        /** 프로그램 + 회차 + 타입 기준 단건 조회(운영자/학생) */
        Optional<ProgramReport> findByProgram_ProgramIdAndSchedule_ScheduleIdAndReportType(
                Long programId, Long scheduleId, ReportType reportType
        );

        /** 프로그램 단위 전체 보고서 목록 */
        List<ProgramReport> findAllByProgram_ProgramIdOrderByCreatedAtDesc(Long programId);

        /** 프로그램 + 타입(학생/운영자) 기준 목록 */
        List<ProgramReport> findAllByProgram_ProgramIdAndReportTypeOrderByCreatedAtDesc(
                Long programId, ReportType reportType
        );

        Page<ProgramReport> findByProgram_ProgramIdAndReportType(
                Long programId, ReportType reportType, Pageable pageable
        );

        Page<ProgramReport> findByProgram_ProgramIdAndReportTypeAndStatus(
                Long programId, ReportType reportType, ReportStatus status, Pageable pageable
        );

        /** 특정 사용자가 작성한 보고서(마이페이지 등에서 활용 가능) */
        @Query("""
            select r
              from ProgramReport r
             where r.writer.id = :userId
               and r.reportType = :reportType
             order by r.createdAt desc
        """)
        Page<ProgramReport> findMyReports(@Param("userId") Long userId,
                                          @Param("reportType") ReportType reportType,
                                          Pageable pageable);

        // 미검토 결과보고서 수 (reviewed = false 라는 필드가 있다고 가정)
        long countByReviewedFalse();

}

//    // 파생 쿼리(정확한 필드 경로 사용) — status가 필수일 때
//    List<ProgramReport> findAllByProgram_programIdAndStatusOrderByCreatedAtDesc(Long programId, ReportStatus status);
//
//    // 파생 쿼리 — status 조건 없이 최신순
//    List<ProgramReport> findAllByProgram_programIdOrderByCreatedAtDesc(Long programId);
//
//    Page<ProgramReport> findByProgram_ProgramId(Long programId, Pageable pageable);
//    Page<ProgramReport> findByProgram_ProgramIdAndWriterType(Long programId, ReportType reportType, Pageable pageable);




