package com.competency.SCMS.repository.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.operation.ProgramSatisfaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface SatisfactionRepository extends JpaRepository<ProgramSatisfaction, Long>, JpaSpecificationExecutor<ProgramSatisfaction> {
}

