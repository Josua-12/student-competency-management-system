package com.competency.scms.repository.noncurricular.operation;

import com.competency.scms.domain.noncurricular.operation.ProgramSatisfaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface SatisfactionRepository extends JpaRepository<ProgramSatisfaction, Long>, JpaSpecificationExecutor<ProgramSatisfaction> {
}

