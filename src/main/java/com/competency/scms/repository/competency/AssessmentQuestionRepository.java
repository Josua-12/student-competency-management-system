package com.competency.scms.repository.competency;

import com.competency.scms.domain.competency.AssessmentQuestion;
import com.competency.scms.domain.competency.Competency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentQuestionRepository extends JpaRepository<AssessmentQuestion, Long> {

    // 특정 역량에 속항 모든 문항 조회
    List<AssessmentQuestion> findByCompetency(Competency competency);

    // 아이디로 조회
    List<AssessmentQuestion> findByCompetencyId(Long competencyId);

}
