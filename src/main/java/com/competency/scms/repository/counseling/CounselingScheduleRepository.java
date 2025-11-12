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
    @Query("SELECT cs FROM CounselingBaseSchedule cs WHERE cs.counselor = :counselor ORDER BY cs.dayOfWeek ASC")
    Page<CounselingBaseSchedule> findByCounselorAndScheduleDateBetween(@Param("counselor") User counselor, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    // 특정 요일에 예약 가능한 상담사들 조회
    Page<CounselingBaseSchedule> findByDayOfWeekAndSlot0910TrueOrSlot1011TrueOrSlot1112TrueOrSlot1314TrueOrSlot1415TrueOrSlot1516TrueOrSlot1617TrueOrSlot1718True(DayOfWeek dayOfWeek, Pageable pageable);
}
