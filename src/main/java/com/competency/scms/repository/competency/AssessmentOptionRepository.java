package com.competency.scms.repository.competency;

import com.competency.scms.domain.competency.AssessmentOption;
import com.competency.scms.domain.competency.AssessmentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentOptionRepository extends JpaRepository<AssessmentOption, Long> {

    //특정 문항에 속한 모든 항목 보기 조회
    List<AssessmentOption> findByQuestion(AssessmentQuestion question);
}
