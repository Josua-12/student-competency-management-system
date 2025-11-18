package com.competency.scms.repository.counseling;

import com.competency.scms.domain.counseling.CounselingSubField;
import com.competency.scms.domain.counseling.CounselingField;
import com.competency.scms.domain.counseling.SatisfactionQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SatisfactionQuestionRepository extends JpaRepository<SatisfactionQuestion, Long> {

    // CNSL-024: 상담만족도 문항관리 - 활성화된 질문 조회
    Page<SatisfactionQuestion> findByIsActiveTrueOrderByDisplayOrderAsc(Pageable pageable);
    
    List<SatisfactionQuestion> findByIsActiveTrueOrderByDisplayOrderAsc();

    // 상담 분야별 질문 조회
    Page<SatisfactionQuestion> findByCounselingFieldAndIsActiveTrueOrderByDisplayOrderAsc(CounselingField counselingField, Pageable pageable);

    // 카테고리별 질문 조회
    Page<SatisfactionQuestion> findByCategoryAndIsActiveTrueOrderByDisplayOrderAsc(CounselingSubField category, Pageable pageable);

    // 시스템 기본 질문 조회
    List<SatisfactionQuestion> findByIsSystemDefaultTrueAndIsActiveTrueOrderByDisplayOrderAsc();
    
    // 시스템 기본 질문 + 특정 상담 분야 질문 조회
    @org.springframework.data.jpa.repository.Query("SELECT q FROM SatisfactionQuestion q WHERE q.isActive = true AND (q.isSystemDefault = true OR q.counselingField = :counselingField) ORDER BY q.displayOrder ASC")
    List<SatisfactionQuestion> findByIsActiveTrueAndCounselingFieldIsNullOrCounselingFieldOrderByDisplayOrderAsc(@org.springframework.data.repository.query.Param("counselingField") CounselingField counselingField);
}
