package com.competency.SCMS.repository.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.operation.Attendance;
import com.competency.SCMS.domain.noncurricular.operation.AttendanceStatus;
import com.competency.SCMS.domain.user.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findAllBySchedule_IdAndStudentIdAndStatus(Long scheduleId,
                                                               Long studentId,
                                                               AttendanceStatus status);

    Optional<Attendance> findBySchedule_IdAndStudentId(Long scheduleId, Long studentId);

    List<Attendance> findAllBySchedule_IdOrderByStudentIdAsc(Long scheduleId);
}
