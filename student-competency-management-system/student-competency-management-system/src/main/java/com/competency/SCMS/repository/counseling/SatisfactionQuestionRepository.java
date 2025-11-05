package com.competency.SCMS.repository;

import com.competency.SCMS.domain.counseling.SatisfactionQuestion;
import com.competency.SCMS.domain.counseling.CounselingField;
import com.competency.SCMS.domain.counseling.CounselingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SatisfactionQuestionRepository extends JpaRepository<SatisfactionQuestion, Long> {
    
    // REQ24: 상담만족도 문항관리 - 활성화된 질문 조회
    List<SatisfactionQuestion> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    // 상담 분야별 질문 조회
    List<SatisfactionQuestion> findByCounselingFieldAndIsActiveTrueOrderByDisplayOrderAsc(CounselingField counselingField);
    
    // 카테고리별 질문 조회
    List<SatisfactionQuestion> findByCategoryAndIsActiveTrueOrderByDisplayOrderAsc(CounselingCategory category);
    
    // 시스템 기본 질문 조회
    List<SatisfactionQuestion> findByIsSystemDefaultTrueAndIsActiveTrueOrderByDisplayOrderAsc();
}