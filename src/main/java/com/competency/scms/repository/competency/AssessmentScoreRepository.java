package com.competency.scms.repository.competency;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssessmentScoreRepository extends JpaRepository<AssessmentScoreRepository, Long> {

    // 비교과 대시보드 관련 필요에 의해 추가 - 2025.11.14 11:38 JHE
    // 특정 진단 결과에 대해, 점수가 낮은 역량 TOP N
    @Query("""
        select s
        from AssessmentScore s
        where s.assessmentResult.id = :resultId
        order by s.score asc
        """)
    List<AssessmentScoreRepository> findWeakCompetencies(Long resultId, Pageable pageable);
}
