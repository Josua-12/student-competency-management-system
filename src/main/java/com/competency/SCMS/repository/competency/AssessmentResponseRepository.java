package com.competency.SCMS.repository.competency;

import com.competency.SCMS.domain.competency.AssessmentResponse;
import com.competency.SCMS.domain.competency.AssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentResponseRepository extends JpaRepository<AssessmentResponse, Long> {
    // 특정 결과지에 속한 모든 답변 조회
    List<AssessmentResponse> findByAssessmentResult(AssessmentResult result);

    // 특정 결과지에 속한 모든 답변 조회 (ID)
    List<AssessmentResponse> findByAssessmentResultId(Long resultId);
}
