package com.competency.SCMS.repository.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.operation.Attendance;
import com.competency.SCMS.domain.noncurricular.operation.AttendanceStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface AttendanceRepo extends JpaRepository<Attendance, Long>, JpaSpecificationExecutor<Attendance> {

    Optional<Attendance> findByScheduleIdAndStudentId(Long scheduleId, Long studentId);
    List<Attendance> findByScheduleIdOrderByStudentIdAsc(Long scheduleId);

    @Query("select a from ProgramAttendance a where a.program.id=:programId and a.status=:status")
    List<Attendance> findAllByProgramAndStatus(@Param("programId") Long programId,
                                                      @Param("status") AttendanceStatus status);

    @Query("select count(a) from ProgramAttendance a where a.schedule.id=:scheduleId and a.status='PRESENT'")
    long countPresentBySchedule(@Param("scheduleId") Long scheduleId);
}
