package com.competency.SCMS.repository.noncurricular.linkCompetency;

import com.competency.SCMS.domain.noncurricular.linkCompetency.LinkCompetency;
import com.competency.SCMS.domain.noncurricular.linkCompetency.CompetencyType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompetencyRepository extends JpaRepository<LinkCompetency, Long> {

    List<LinkCompetency> findAllByProgram_IdAndCompetency_ActiveTrueOrderByCompetency_NameAsc(Long programId);
}



