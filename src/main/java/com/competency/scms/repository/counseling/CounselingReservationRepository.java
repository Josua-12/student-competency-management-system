package com.competency.scms.repository.counseling;

import com.competency.scms.domain.counseling.CounselingField;
import com.competency.scms.domain.counseling.CounselingReservation;
import com.competency.scms.domain.counseling.ReservationStatus;
import com.competency.scms.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CounselingReservationRepository extends JpaRepository<CounselingReservation, Long> {

    // CNSL-002: (학생) 학생별 상담 예약 목록 조회
    Page<CounselingReservation> findByStudentOrderByCreatedAtDesc(User student, Pageable pageable);

    // CNSL-002, CNSL-016: 학생별 + 상담 분야별 예약 조회
    Page<CounselingReservation> findByStudentAndCounselingFieldOrderByCreatedAtDesc(User student, CounselingField counselingField, Pageable pageable);

    // CNSL-008 : (관리자) 상담 승인 관리 - 모든 대기 중인 예약 조회
    Page<CounselingReservation> findByStatusOrderByCreatedAtAsc(ReservationStatus status, Pageable pageable);

    // CNSL-009: (상담사) 상담 승인 관리 - 본인에게 배정된 대기 중인 예약 조회
    Page<CounselingReservation> findByCounselorAndStatusOrderByCreatedAtAsc(User counselor, ReservationStatus status, Pageable pageable);

    // CNSL-011: 상담사별 특정상태의 상담 일정 조회 (배정된 상태:CONFIRMED)
    @Query("SELECT cr FROM CounselingReservation cr WHERE cr.counselor = :counselor AND cr.status = :status ORDER BY cr.reservationDate ASC, cr.startTime ASC")
    Page<CounselingReservation> findByCounselorAndStatusOrderByConfirmedDateTimeAsc(@Param("counselor") User counselor, @Param("status") ReservationStatus status, Pageable pageable);

    // CNSL-015: 전체 상담 이력 조회 (검색 기능 포함)
    @Query("SELECT cr FROM CounselingReservation cr " +
            "WHERE (:keyword IS NULL OR :keyword = '' " +   // 키워드 미입력시 전체 조회
            "OR cr.student.name LIKE %:keyword% " +
            "OR cr.counselor.name LIKE %:keyword%)")
    Page<CounselingReservation> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // CNSL-016: 상담사별 전체 상담 이력 조회
    Page<CounselingReservation> findByCounselorOrderByCreatedAtDesc(User counselor, Pageable pageable);

    // CNSL-017: 상담사 본인 담당 상담 현황
    @Query("SELECT cr FROM CounselingReservation cr WHERE cr.counselor = :counselor AND cr.status IN :statuses ORDER BY cr.reservationDate ASC, cr.startTime ASC")
    Page<CounselingReservation> findByCounselorAndStatusIn(@Param("counselor") User counselor, @Param("statuses") List<ReservationStatus> statuses, Pageable pageable);

    // CNSL-018: 상담 유형별 통계     //특정 상태(status)인 상담 예약들을 상담 유형(counselingField)별로 몇 건씩 있는지 세는 통계
    @Query("SELECT cr.counselingField, COUNT(cr) FROM CounselingReservation cr WHERE cr.status = :status GROUP BY cr.counselingField")
    List<Object[]> countByStatusGroupByCounselingField(@Param("status") ReservationStatus status);

    // CNSL-019: 전체 상담 현황 통계
    @Query("SELECT cr.status, COUNT(cr) FROM CounselingReservation cr GROUP BY cr.status")
    List<Object[]> countGroupByStatus();

    // CNSL-021: 상담원별 현황
    @Query("SELECT cr.counselor, cr.status, COUNT(cr) FROM CounselingReservation cr WHERE cr.counselor IS NOT NULL GROUP BY cr.counselor, cr.status")
    Page<Object[]> countByCounselorGroupByStatus(Pageable pageable);

    // 기간별 조회 (통계용)
    @Query("SELECT cr FROM CounselingReservation cr WHERE cr.createdAt BETWEEN :startDate AND :endDate ORDER BY cr.createdAt DESC")
    Page<CounselingReservation> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // 테스트용: createdAt 직접 업데이트
    @Modifying
    @Transactional
    @Query(value = "UPDATE counseling_reservations SET created_at = :createdAt WHERE id = :id", nativeQuery = true)
    void updateCreatedAt(@Param("id") Long id, @Param("createdAt") LocalDateTime createdAt);
}
