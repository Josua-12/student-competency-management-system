package com.competency.SCMS.repository.counseling;

import com.competency.SCMS.domain.counseling.CounselingSatisfaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CounselingSatisfactionRepository extends JpaRepository<CounselingSatisfaction, Long> {

    // 예약별 만족도 조회
    Optional<CounselingSatisfaction> findByReservationId(Long reservationId);

    // CNSL-020: 상담사별 만족도 결과 조회
    List<CounselingSatisfaction> findByCounselorOrderBySubmittedAtDesc(User counselor);

    // 기간별 만족도 조회
    @Query("SELECT cs FROM CounselingSatisfaction cs WHERE cs.submittedAt BETWEEN :startDate AND :endDate ORDER BY cs.submittedAt DESC")
    List<CounselingSatisfaction> findBySubmittedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // 상담사별 기간별 만족도 조회
    @Query("SELECT cs FROM CounselingSatisfaction cs WHERE cs.counselor = :counselor AND cs.submittedAt BETWEEN :startDate AND :endDate ORDER BY cs.submittedAt DESC")
    List<CounselingSatisfaction> findByCounselorAndSubmittedAtBetween(@Param("counselor") User counselor, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
