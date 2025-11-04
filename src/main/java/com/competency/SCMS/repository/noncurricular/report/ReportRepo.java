package com.competency.SCMS.repository.noncurricular.report;

import com.competency.SCMS.domain.noncurricular.report.Report;
import com.competency.SCMS.domain.noncurricular.report.ReportStatus;
import com.competency.SCMS.domain.noncurricular.report.ReportType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ReportRepo extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {

    List<Report> findByProgramIdAndType(Long programId, ReportType type);

    Optional<Report> findByProgramIdAndWriterUserIdAndType(Long programId, Long writerUserId, ReportType type);

    @Query("select r from ProgramReport r where r.program.id=:programId and (:status is null or r.status=:status) order by r.createdAt desc")
    List<Report> findAllByProgramAndStatus(@Param("programId") Long programId, @Param("status") ReportStatus status);
}

