package com.competency.SCMS.repository.noncurricular.linkCompetency;

import com.competency.SCMS.domain.noncurricular.linkCompetency.LinkCompetency;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LinkCompetencyRepository extends JpaRepository<LinkCompetency, Long> {

    List<LinkCompetency> findAllByProgram_IdAndCompetency_ActiveTrueOrderByCompetency_NameAsc(Long programId);
    boolean existsByCode(String code);
    List<LinkCompetency> findAllByProgram_programId(Long programId);
}



