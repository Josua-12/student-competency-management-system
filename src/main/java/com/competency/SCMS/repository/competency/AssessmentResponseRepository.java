package com.competency.SCMS.repository.competency;

import com.competency.SCMS.domain.competency.AssessmentResponse;
import com.competency.SCMS.domain.competency.AssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssessmentResponseRepository extends JpaRepository<AssessmentResponse, Long> {
    // 특정 결과지에 속한 모든 답변 조회
    List<AssessmentResponse> findByAssessmentResult(AssessmentResult result);

    // 특정 결과지에 속한 모든 답변 조회 (ID)
    List<AssessmentResponse> findByAssessmentResultId(Long resultId);

    /**
     * 결과 계산을 위해 필요한 모든 연관 엔티티를 Fetch Join
     * 1. 응답(Response)
     * 2. 응답이 선택한 보기(Option) - 점수(score) 획득용
     * 3. 응답이 속한 문항(Question)
     * 4. 문항이 속한 역량(Competency) - 하위역량
     * 5. 하위 역량의 부모 역량(Parent) - 핵심역량
     */
    @Query("SELECT r FROM AssessmentResponse r " +
            "JOIN FETCH r.assessmentOption o " +
            "JOIN FETCH r.question q " +
            "JOIN FETCH q.competency c " +
            "LEFT JOIN FETCH c.parent p " +
            "WHERE r.assessmentResult.id = :resultId")
    List<AssessmentResponse> findAllWithDetailsByResultId(@Param("resultId") Long resultId);
}
