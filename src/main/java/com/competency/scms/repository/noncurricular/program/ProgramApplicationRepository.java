package com.competency.scms.repository.noncurricular.program;

import com.competency.scms.domain.noncurricular.operation.ProgramApplication;
import com.competency.scms.domain.noncurricular.operation.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramApplicationRepository extends JpaRepository<ProgramApplication, Long> {

    int countByProgram_IdAndStatus(Long programId, ApprovalStatus status);
}
