package com.competency.SCMS.repository.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.operation.ProgramApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalHistoryRepository extends JpaRepository<ProgramApprovalHistory, Long> {
    List<ProgramApprovalHistory> findAllByProgram_IdOrderByCreatedAtDesc(Long programId);
}
