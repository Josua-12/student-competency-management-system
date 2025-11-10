//package com.competency.SCMS.repository.counseling;
//
//import com.competency.SCMS.domain.counseling.CounselingField;
//import com.competency.SCMS.domain.counseling.Counselor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface CounselorRepository extends JpaRepository<Counselor, User> {
//    // CNSL-023: 상담원관리 - 활성화된 상담사 조회
//    Page<Counselor> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
//
//    // 상담 분야별 활성화된 상담사 조회
//    Page<Counselor> findByCounselingFieldAndIsActiveTrueOrderByCreatedAtDesc(CounselingField counselingField, Pageable pageable);
//
//    // 사용자 ID로 상담사 정보 조회
//    Optional<Counselor> findByCounselorId(User user);
//}
