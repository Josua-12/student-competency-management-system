package com.competency.SCMS.repository.counseling;

import com.competency.SCMS.domain.counseling.CounselingSubField;
import com.competency.SCMS.domain.counseling.CounselingField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CounselingSubFieldRepository extends JpaRepository<CounselingSubField, Long> {

    // CNSL-022: 상담분류관리 - 활성화된 카테고리 조회
    Page<CounselingSubField> findByIsActiveTrueOrderByCounselingFieldAscSubfieldNameAsc(Pageable pageable);

    // 상담 분야별 활성화된 카테고리 조회
    Page<CounselingSubField> findByCounselingFieldAndIsActiveTrueOrderBySubfieldNameAsc(CounselingField counselingField, Pageable pageable);

}
