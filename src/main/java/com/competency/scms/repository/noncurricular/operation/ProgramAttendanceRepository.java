package com.competency.scms.repository.noncurricular.operation;

import com.competency.scms.domain.noncurricular.operation.ProgramAttendance;
import com.competency.scms.domain.noncurricular.operation.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.*;

// src/main/java/com/competency/scms/repository/noncurricular/operation/ProgramAttendanceRepository.java

public interface ProgramAttendanceRepository extends JpaRepository<ProgramAttendance, Long> {


    List<ProgramAttendance> findAllBySchedule_ScheduleIdAndStudent_IdAndStatus(
            Long scheduleId, Long studentId, AttendanceStatus status);


    Optional<ProgramAttendance> findBySchedule_ScheduleIdAndStudent_Id(
            Long scheduleId, Long studentId);


    List<ProgramAttendance> findAllBySchedule_ScheduleIdOrderByStudent_IdAsc(Long scheduleId);

    //  학생별 출석 이력
    Page<ProgramAttendance> findByStudent_Id(Long studentId, Pageable pageable);

    // 특정 일자/상태(당일 00:00~23:59 범위)
    @Query("""
        select a from ProgramAttendance a
        where a.schedule.scheduleId = :scheduleId
          and a.attendedAt between :from and :to
    """)
    List<ProgramAttendance> findByScheduleAndAttendedAtBetween(
            @Param("scheduleId") Long scheduleId,
            @Param("from") java.time.LocalDateTime from,
            @Param("to")   java.time.LocalDateTime to
    );

    // 상태 카운트
    long countBySchedule_ScheduleIdAndStatus(Long scheduleId, AttendanceStatus status);


    // 중복 방지 (회차+학생+시간대 겹침 확인)
    boolean existsBySchedule_ScheduleIdAndStudent_IdAndAttendedAtBetween(
            Long scheduleId, Long studentId,
            java.time.LocalDateTime from, java.time.LocalDateTime to);


    // 금일 출석 관리 대상 건수 - attendedAt 기반으로 수정
    @Query("SELECT COUNT(a) FROM ProgramAttendance a WHERE DATE(a.attendedAt) = :attendDate")
    long countByAttendDate(@Param("attendDate") LocalDate attendDate);

    // 특정 상태의 출석 건수
    long countByStatus(AttendanceStatus status);


}

