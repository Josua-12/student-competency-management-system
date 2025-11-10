package com.competency.SCMS.repository.counseling;

import com.competency.SCMS.domain.counseling.CounselingSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CounselingScheduleRepository extends JpaRepository<CounselingSchedule, Long> {

    // CNSL-006: 상담사별 일정 조회
    Page<CounselingSchedule> findByCounselorOrderByScheduleDateAsc(User counselor, Pageable pageable);

    // CNSL-007: 특정 날짜의 상담사 일정 조회
    Page<CounselingSchedule> findByCounselorAndScheduleDate(User counselor, LocalDate scheduleDate, Pageable pageable);

    // 기간별 상담사 일정 조회
    @Query("SELECT cs FROM CounselingSchedule cs WHERE cs.counselor = :counselor AND cs.scheduleDate BETWEEN :startDate AND :endDate ORDER BY cs.scheduleDate ASC")
    Page<CounselingSchedule> findByCounselorAndScheduleDateBetween(@Param("counselor") User counselor, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    // 특정 날짜에 예약 가능한 상담사들 조회
    @Query("SELECT cs FROM CounselingSchedule cs WHERE cs.scheduleDate = :date AND (cs.slot0910 = true OR cs.slot1011 = true OR cs.slot1112 = true OR cs.slot1213 = true OR cs.slot1314 = true OR cs.slot1415 = true OR cs.slot1516 = true OR cs.slot1617 = true OR cs.slot1718 = true)")
    Page<CounselingSchedule> findAvailableCounselorsOnDate(@Param("date") LocalDate date, Pageable pageable);
}
