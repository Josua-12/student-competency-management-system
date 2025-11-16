package com.competency.scms.repository.counseling;

import com.competency.scms.domain.counseling.CounselingField;
import com.competency.scms.domain.counseling.Counselor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CounselorRepository extends JpaRepository<Counselor, Long> {
    // CNSL-023: 상담원관리 - 활성화된 상담사 조회
    Page<Counselor> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    // 상담 분야별 활성화된 상담사 조회
    Page<Counselor> findByCounselingFieldAndIsActiveTrueOrderByCreatedAtDesc(CounselingField counselingField, Pageable pageable);
    
    // 상담 분야별 활성화된 상담사 목록 조회 (페이징 없음)
    List<Counselor> findByCounselingFieldAndIsActiveTrue(CounselingField counselingField);

    // 사용자 ID로 상담사 정보 조회
    Optional<Counselor> findByCounselorId(Long counselorId);
    
    // User로 상담사 정보 조회
    Optional<Counselor> findByUser(com.competency.scms.domain.user.User user);

    // 대시보드 분리를 위한 상담사 조회 메서드
    List<Counselor> findAllByCounselorId(Long counselorId); // CounselRepository

    // 활성화 상태별 상담사 수 조회
    long countByIsActive(boolean isActive);

}
