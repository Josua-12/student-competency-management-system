package com.competency.scms.repository.counseling;

import com.competency.scms.domain.counseling.CounselingBaseSchedule;
import com.competency.scms.domain.counseling.CounselingOverrideSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.competency.scms.domain.user.User;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CounselingScheduleRepository extends JpaRepository<CounselingBaseSchedule, Long> {

    // CNSL-006: 상담사별 일정 조회 (생성일순)
    Page<CounselingBaseSchedule> findByCounselorOrderByCreatedAtDesc(User counselor, Pageable pageable);

    // CNSL-007: 특정 요일의 상담사 일정 조회
    Optional<CounselingBaseSchedule> findByCounselorAndDayOfWeek(User counselor, DayOfWeek dayOfWeek);

    // 상담사별 일정 조회 (요일순)
    @Query("SELECT cs FROM CounselingBaseSchedule cs WHERE cs.counselor = :counselor ORDER BY cs.dayOfWeek ASC")
    Page<CounselingBaseSchedule> findByCounselorOrderByDayOfWeek(@Param("counselor") User counselor, Pageable pageable);

    // 특정 요일에 예약 가능한 상담사들 조회
    @Query("SELECT cs FROM CounselingBaseSchedule cs WHERE cs.dayOfWeek = :dayOfWeek AND (cs.slot0910 = true OR cs.slot1011 = true OR cs.slot1112 = true OR cs.slot1314 = true OR cs.slot1415 = true OR cs.slot1516 = true OR cs.slot1617 = true OR cs.slot1718 = true)")
    Page<CounselingBaseSchedule> findAvailableSchedulesByDayOfWeek(@Param("dayOfWeek") DayOfWeek dayOfWeek, Pageable pageable);

    // 특정 날짜의 예외 스케줄 조회
    @Query("SELECT os FROM CounselingOverrideSchedule os WHERE os.counselor = :counselor AND :date BETWEEN os.startDate AND os.endDate")
    List<CounselingOverrideSchedule> findOverrideSchedules(@Param("counselor") User counselor, @Param("date") LocalDate date);
}
