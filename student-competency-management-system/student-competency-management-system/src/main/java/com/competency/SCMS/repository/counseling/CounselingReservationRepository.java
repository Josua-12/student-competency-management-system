package com.competency.SCMS.repository;

import com.competency.SCMS.domain.counseling.CounselingReservation;
import com.competency.SCMS.domain.counseling.CounselingField;
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
public interface CounselingReservationRepository extends JpaRepository<CounselingReservation, Long> {
    
    // REQ2: 학생 상담 예약 목록 조회
    List<CounselingReservation> findByStudentOrderByCreatedAtDesc(User student);
    
    // REQ8, REQ9: 상담 승인 관리 - 대기 중인 예약 조회
    List<CounselingReservation> findByStatusOrderByCreatedAtAsc(CounselingReservation.ReservationStatus status);
    
    // REQ11: 배정된 상담 일정 조회 (상담사별)
    List<CounselingReservation> findByCounselorAndStatusOrderByConfirmedDateTimeAsc(User counselor, CounselingReservation.ReservationStatus status);
    
    // REQ15: 전체 상담 이력 조회 (페이징)
    Page<CounselingReservation> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // REQ16: 학생별 상담 이력 조회
    List<CounselingReservation> findByStudentOrderByCreatedAtDesc(User student);
    
    // REQ16: 상담사별 상담 이력 조회
    List<CounselingReservation> findByCounselorOrderByCreatedAtDesc(User counselor);
    
    // REQ17: 상담사 본인 담당 상담 현황
    @Query("SELECT cr FROM CounselingReservation cr WHERE cr.counselor = :counselor AND cr.status IN :statuses ORDER BY cr.confirmedDateTime ASC")
    List<CounselingReservation> findByCounselorAndStatusIn(@Param("counselor") User counselor, @Param("statuses") List<CounselingReservation.ReservationStatus> statuses);
    
    // REQ18: 상담 유형별 통계
    @Query("SELECT cr.counselingField, COUNT(cr) FROM CounselingReservation cr WHERE cr.status = :status GROUP BY cr.counselingField")
    List<Object[]> countByStatusGroupByCounselingField(@Param("status") CounselingReservation.ReservationStatus status);
    
    // REQ19: 전체 상담 현황 통계
    @Query("SELECT cr.status, COUNT(cr) FROM CounselingReservation cr GROUP BY cr.status")
    List<Object[]> countGroupByStatus();
    
    // REQ21: 상담원별 현황
    @Query("SELECT cr.counselor, cr.status, COUNT(cr) FROM CounselingReservation cr WHERE cr.counselor IS NOT NULL GROUP BY cr.counselor, cr.status")
    List<Object[]> countByCounselorGroupByStatus();
    
    // 기간별 조회 (통계용)
    @Query("SELECT cr FROM CounselingReservation cr WHERE cr.createdAt BETWEEN :startDate AND :endDate ORDER BY cr.createdAt DESC")
    List<CounselingReservation> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}