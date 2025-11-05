package com.competency.SCMS.repository.noncurricular.linkCompetency;

import com.competency.SCMS.domain.noncurricular.linkCompetency.LinkCompetency;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ProgramCompetencyRepository
        extends JpaRepository<LinkCompetency, Long>, JpaSpecificationExecutor<LinkCompetency> {

    // 프로그램별 매핑을 가중치순으로
    List<LinkCompetency> findAllByProgram_IdOrderByWeightPercentDesc(Long programId);

    // 역량 기준으로
    List<LinkCompetency> findAllByCompetency_Id(Long competencyId);
}


