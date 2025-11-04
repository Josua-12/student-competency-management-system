package com.competency.SCMS.repository;

import com.competency.SCMS.domain.counseling.CounselingRecord;
import com.competency.SCMS.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CounselingRecordRepository extends JpaRepository<CounselingRecord, Long> {
    
    // REQ13: 상담사별 상담일지 목록 조회
    List<CounselingRecord> findByCounselorOrderByCreatedAtDesc(User counselor);
    
    // REQ15: 전체 상담 이력 조회 (페이징)
    Page<CounselingRecord> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // REQ16: 학생별 상담일지 조회
    List<CounselingRecord> findByStudentOrderByCreatedAtDesc(User student);
    
    // REQ16: 상담사별 상담일지 조회
    List<CounselingRecord> findByCounselorOrderByCreatedAtDesc(User counselor);
    
    // 기간별 상담일지 조회
    @Query("SELECT cr FROM CounselingRecord cr WHERE cr.counselingDate BETWEEN :startDate AND :endDate ORDER BY cr.counselingDate DESC")
    List<CounselingRecord> findByCounselingDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // 상담사별 기간별 상담일지 조회
    @Query("SELECT cr FROM CounselingRecord cr WHERE cr.counselor = :counselor AND cr.counselingDate BETWEEN :startDate AND :endDate ORDER BY cr.counselingDate DESC")
    List<CounselingRecord> findByCounselorAndCounselingDateBetween(@Param("counselor") User counselor, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}