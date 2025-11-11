package com.competency.scms.repository.noncurricular.operation;

import com.competency.scms.domain.noncurricular.operation.ProgramApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalHistoryRepository extends JpaRepository<ProgramApprovalHistory, Long> {
    List<ProgramApprovalHistory> findAllByProgram_ProgramIdOrderByCreatedAtDesc(Long programId);
}
