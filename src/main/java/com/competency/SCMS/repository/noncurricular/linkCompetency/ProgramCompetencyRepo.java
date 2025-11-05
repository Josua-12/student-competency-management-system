package com.competency.SCMS.repository.noncurricular.linkCompetency;

import com.competency.SCMS.domain.noncurricular.linkCompetency.Competency;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ProgramCompetencyRepo extends JpaRepository<Competency, Long>, JpaSpecificationExecutor<Competency> {

    boolean existsByProgramIdAndCompetencyId(Long programId, Long competencyId);
    Optional<Competency> findByProgramIdAndCompetencyId(Long programId, Long competencyId);

    @Query("select pc from ProgramCompetency pc where pc.program.id=:programId order by pc.weightPercent desc")
    List<Competency> findAllByProgramOrderByWeight(@Param("programId") Long programId);

    List<Competency> findAllByCompetencyId(Long competencyId);
}

