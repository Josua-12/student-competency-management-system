package com.competency.SCMS.repository.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.operation.ProgramAttendance;
import com.competency.SCMS.domain.noncurricular.operation.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.time.LocalDate;
import java.util.*;

public interface ProgramAttendanceRepository extends JpaRepository<ProgramAttendance, Long> {

    List<ProgramAttendance> findAllBySchedule_IdAndStudentIdAndStatus(Long scheduleId,
                                                                      Long studentId,
                                                                      AttendanceStatus status);

    Optional<ProgramAttendance> findBySchedule_IdAndStudentId(Long scheduleId, Long studentId);

    List<ProgramAttendance> findAllBySchedule_IdOrderByStudentIdAsc(Long scheduleId);

    // 회차별 출석부 (이전에 너가 겪은 오류 피하려면 'student' 엔티티명 + 'userId' 경로 정확히!)
    List<ProgramAttendance> findAllBySchedule_ScheduleIdOrderByStudent_UserIdAsc(Long scheduleId);

    // 학생별 출석 이력
    Page<ProgramAttendance> findByStudent_UserId(Long studentId, Pageable pageable);

    // 특정 일자/상태
    List<ProgramAttendance> findBySchedule_ScheduleIdAndAttendDate(Long scheduleId, LocalDate date);
    long countBySchedule_ScheduleIdAndStatus(Long scheduleId, AttendanceStatus status);

    // 중복 방지 (회차+학생+일자 유니크)
    boolean existsBySchedule_ScheduleIdAndStudent_UserIdAndAttendDate(Long scheduleId, Long studentId, LocalDate date);

}
