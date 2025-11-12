package com.competency.scms.repository.noncurricular.report;

import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.noncurricular.report.ProgramReport;
import com.competency.scms.domain.noncurricular.report.ReportStatus;
import org.springframework.data.jpa.repository.*;

import java.util.List;

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
}

//    // 파생 쿼리(정확한 필드 경로 사용) — status가 필수일 때
//    List<ProgramReport> findAllByProgram_programIdAndStatusOrderByCreatedAtDesc(Long programId, ReportStatus status);
//
//    // 파생 쿼리 — status 조건 없이 최신순
//    List<ProgramReport> findAllByProgram_programIdOrderByCreatedAtDesc(Long programId);
//
//    Page<ProgramReport> findByProgram_ProgramId(Long programId, Pageable pageable);
//    Page<ProgramReport> findByProgram_ProgramIdAndWriterType(Long programId, ReportType reportType, Pageable pageable);




