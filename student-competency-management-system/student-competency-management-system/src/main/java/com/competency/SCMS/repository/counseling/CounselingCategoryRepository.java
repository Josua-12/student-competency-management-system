package com.competency.SCMS.repository;

import com.competency.SCMS.domain.counseling.CounselingCategory;
import com.competency.SCMS.domain.counseling.CounselingField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CounselingCategoryRepository extends JpaRepository<CounselingCategory, Long> {
    
    // REQ22: 상담분류관리 - 활성화된 카테고리 조회
    List<CounselingCategory> findByIsActiveTrueOrderByCounselingFieldAscCategoryNameAsc();
    
    // 상담 분야별 카테고리 조회
    List<CounselingCategory> findByCounselingFieldAndIsActiveTrueOrderByCategoryNameAsc(CounselingField counselingField);
    
    // 카테고리명으로 검색
    List<CounselingCategory> findByCategoryNameContainingAndIsActiveTrueOrderByCategoryNameAsc(String categoryName);
}