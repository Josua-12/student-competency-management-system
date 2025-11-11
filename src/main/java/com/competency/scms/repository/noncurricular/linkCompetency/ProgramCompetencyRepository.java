package com.competency.scms.repository.noncurricular.linkCompetency;

import com.competency.scms.domain.noncurricular.linkCompetency.LinkCompetency;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface ProgramCompetencyRepository
        extends JpaRepository<LinkCompetency, Long>, JpaSpecificationExecutor<LinkCompetency> {

    List<LinkCompetency> findByProgram_ProgramId(Long programId);
    boolean existsByProgram_ProgramIdAndCompetency_Id(Long programId, Long competencyId);
    void deleteByProgram_ProgramId(Long programId);
}


