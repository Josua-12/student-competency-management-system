package com.competency.SCMS.repository.counseling;

import com.competency.SCMS.domain.counseling.CounselingCategory;
import com.competency.SCMS.domain.counseling.CounselingField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CounselingCategoryRepository extends JpaRepository<CounselingCategory, Long> {

    // CNSL-022: 상담분류관리 - 활성화된 카테고리 조회
    Page<CounselingCategory> findByIsActiveTrueOrderByCounselingFieldAscCategoryNameAsc(Pageable pageable);

    // 상담 분야별 활성화된 카테고리 조회
    Page<CounselingCategory> findByCounselingFieldAndIsActiveTrueOrderByCategoryNameAsc(CounselingField counselingField, Pageable pageable);

}
