package com.competency.SCMS.repository;

import com.competency.SCMS.domain.counseling.Counselor;
import com.competency.SCMS.domain.counseling.CounselingField;
import com.competency.SCMS.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CounselorRepository extends JpaRepository<Counselor, User> {
    
    // REQ23: 상담원관리 - 활성화된 상담사 조회
    List<Counselor> findByIsActiveTrueOrderByCreatedAtDesc();
    
    // 상담 분야별 활성화된 상담사 조회
    List<Counselor> findByCounselingFieldAndIsActiveTrueOrderByCreatedAtDesc(CounselingField counselingField);
    
    // 사용자 ID로 상담사 정보 조회
    Optional<Counselor> findByCounselorId(User user);
}