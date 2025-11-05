package com.competency.SCMS.repository.noncurricular.report;

import com.competency.SCMS.domain.noncurricular.report.ProgramReport;
import com.competency.SCMS.domain.noncurricular.report.ReportStatus;
import com.competency.SCMS.domain.noncurricular.report.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProgramReportRepository
        extends JpaRepository<ProgramReport, Long>, JpaSpecificationExecutor<ProgramReport> {

    // status가 선택값(null 허용)인 JPQL 버전
    @Query("""
        select r
        from Report r
        where r.program.id = :programId
          and (:status is null or r.status = :status)
        order by r.createdAt desc
    """)
    List<ProgramReport> findAllByProgramAndStatus(@Param("programId") Long programId,
                                                  @Param("status") ReportStatus status);

    // 파생 쿼리(정확한 필드 경로 사용) — status가 필수일 때
    List<ProgramReport> findAllByProgram_IdAndStatusOrderByCreatedAtDesc(Long programId, ReportStatus status);

    // 파생 쿼리 — status 조건 없이 최신순
    List<ProgramReport> findAllByProgram_IdOrderByCreatedAtDesc(Long programId);

    Page<ProgramReport> findByProgram_ProgramId(Long programId, Pageable pageable);
    Page<ProgramReport> findByProgram_ProgramIdAndWriterType(Long programId, ReportType reportType, Pageable pageable);

}


