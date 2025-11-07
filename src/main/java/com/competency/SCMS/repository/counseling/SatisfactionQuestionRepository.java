package com.competency.SCMS.repository.counseling;

import com.competency.SCMS.domain.counseling.CounselingSubField;
import com.competency.SCMS.domain.counseling.CounselingField;
import com.competency.SCMS.domain.counseling.SatisfactionQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SatisfactionQuestionRepository extends JpaRepository<SatisfactionQuestion, Long> {

    // CNSL-024: 상담만족도 문항관리 - 활성화된 질문 조회
    Page<SatisfactionQuestion> findByIsActiveTrueOrderByDisplayOrderAsc(Pageable pageable);

    // 상담 분야별 질문 조회
    Page<SatisfactionQuestion> findByCounselingFieldAndIsActiveTrueOrderByDisplayOrderAsc(CounselingField counselingField, Pageable pageable);

    // 카테고리별 질문 조회
    Page<SatisfactionQuestion> findByCategoryAndIsActiveTrueOrderByDisplayOrderAsc(CounselingSubField category, Pageable pageable);

    // 시스템 기본 질문 조회
    List<SatisfactionQuestion> findByIsSystemDefaultTrueAndIsActiveTrueOrderByDisplayOrderAsc();
}
