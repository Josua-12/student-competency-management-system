package com.competency.SCMS.repository.counseling;

import com.competency.SCMS.domain.counseling.CounselingBaseSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.competency.SCMS.domain.user.User;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CounselingScheduleRepository extends JpaRepository<CounselingBaseSchedule, Long> {

    // CNSL-006: 상담사별 일정 조회
    Page<CounselingBaseSchedule> findByCounselorOrderByCreatedAtDesc(User counselor, Pageable pageable);

    // CNSL-007: 특정 요일의 상담사 일정 조회
    Optional<CounselingBaseSchedule> findByCounselorAndDayOfWeek(User counselor, DayOfWeek dayOfWeek);

    // 기간별 상담사 일정 조회
    @Query("SELECT cs FROM CounselingSchedule cs WHERE cs.counselor = :counselor AND cs.scheduleDate BETWEEN :startDate AND :endDate ORDER BY cs.scheduleDate ASC")
    Page<CounselingBaseSchedule> findByCounselorAndScheduleDateBetween(@Param("counselor") User counselor, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    // 특정 날짜에 예약 가능한 상담사들 조회
    @Query("SELECT cs FROM CounselingSchedule cs WHERE cs.scheduleDate = :date AND (cs.slot0910 = true OR cs.slot1011 = true OR cs.slot1112 = true OR cs.slot1213 = true OR cs.slot1314 = true OR cs.slot1415 = true OR cs.slot1516 = true OR cs.slot1617 = true OR cs.slot1718 = true)")
    Page<CounselingBaseSchedule> findAvailableCounselorsOnDate(@Param("date") LocalDate date, Pageable pageable);
}
