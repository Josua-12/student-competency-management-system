package com.competency.scms.repository.noncurricular.linkCompetency;

import com.competency.scms.domain.noncurricular.linkCompetency.LinkCompetency;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface ProgramCompetencyRepository
        extends JpaRepository<LinkCompetency, Long>, JpaSpecificationExecutor<LinkCompetency> {

    List<LinkCompetency> findByProgram_ProgramId(Long programId);
    boolean existsByProgram_ProgramIdAndCompetency_Id(Long programId, Long competencyId);
    void deleteByProgram_ProgramId(Long programId);

    // 내가 약한 역량 목록(competencyId 리스트)을 넣으면
    // 그 역량과 매핑된 프로그램들을 매칭 개수 기준으로 정렬해서 가져오는 예시
    @Query("""
        select pc.program.id as programId,
               count(pc) as matchedCount
        from ProgramCompetency pc
        where pc.competency.id in :competencyIds
        group by pc.program.id
        order by matchedCount desc
        """)
    List<ProgramRecommendationProjection> findRecommendedPrograms(
            List<Long> competencyIds,
            Pageable pageable
    );
}


