package com.competency.SCMS.repository.noncurricular.linkCompetency;

import com.competency.SCMS.domain.noncurricular.linkCompetency.Competency;
import com.competency.SCMS.domain.noncurricular.linkCompetency.CompetencyType;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface CompetencyRepo extends JpaRepository<Competency, Long>, JpaSpecificationExecutor<Competency> {
    Optional<Competency> findByCode(String code);
    List<Competency> findByActiveTrueOrderByNameAsc();
    List<Competency> findByTypeAndActiveTrueOrderByNameAsc(CompetencyType type);
    boolean existsByCode(String code);
}

