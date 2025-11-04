package com.competency.SCMS.repository;

import com.competency.SCMS.domain.counseling.QuestionOption;
import com.competency.SCMS.domain.counseling.SatisfactionQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
    
    // 질문별 활성화된 옵션 조회
    List<QuestionOption> findByQuestionAndIsActiveTrueOrderByDisplayOrderAsc(SatisfactionQuestion question);
    
    // 질문별 모든 옵션 조회 (관리용)
    List<QuestionOption> findByQuestionOrderByDisplayOrderAsc(SatisfactionQuestion question);
}