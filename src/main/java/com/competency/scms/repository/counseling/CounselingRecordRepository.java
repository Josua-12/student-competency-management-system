package com.competency.scms.repository.counseling;

import com.competency.scms.domain.counseling.CounselingRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.competency.scms.domain.user.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CounselingRecordRepository extends JpaRepository<CounselingRecord, Long> {


    // CNSL-013: (상담사) 본인의 상담일지 목록 조회
    Page<CounselingRecord> findByCounselorOrderByCreatedAtDesc(User counselor, Pageable pageable);

    // CNSL-013-1: (상담사) 본인이 담당한 학생별 상담일지 조회
    Page<CounselingRecord> findByCounselorAndStudentOrderByCreatedAtDesc(User counselor, User student, Pageable pageable);

    // CNSL-013-2: (관리자) 공개된 상담 일지 전체 조회
    Page<CounselingRecord> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);

    // CNSL-013-2-1: (관리자) 기간별 공개된 상담일지 전체 조회
    @Query("SELECT cr FROM CounselingRecord cr WHERE cr.counselingDate BETWEEN :startDate AND :endDate ORDER BY cr.counselingDate DESC")
    Page<CounselingRecord> findByCounselingByIsPublicTrueDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // CNSL-013-1-1: (상담사) 본인이 담당한 기간별 상담일지 조회
    @Query("SELECT cr FROM CounselingRecord cr WHERE cr.counselor = :counselor AND cr.counselingDate BETWEEN :startDate AND :endDate ORDER BY cr.counselingDate DESC")
    Page<CounselingRecord> findByCounselorAndCounselingDateBetween(@Param("counselor") User counselor, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
}

