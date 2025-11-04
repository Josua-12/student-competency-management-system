package com.competency.SCMS.repository.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.operation.Satisfaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface SatisfactionRepo extends JpaRepository<Satisfaction, Long>, JpaSpecificationExecutor<Satisfaction> {

    Optional<Satisfaction> findByProgramIdAndScheduleIdAndStudentId(Long programId, Long scheduleId, Long studentId);

    @Query("select avg(s.rating) from ProgramSatisfaction s where s.program.id=:programId")
    Double findAverageRatingByProgram(@Param("programId") Long programId);

    @Query("select avg(s.rating) from ProgramSatisfaction s where s.schedule.id=:scheduleId")
    Double findAverageRatingBySchedule(@Param("scheduleId") Long scheduleId);

    @Query("select s from ProgramSatisfaction s where s.program.id=:programId order by s.createdAt desc")
    List<Satisfaction> findRecentByProgram(@Param("programId") Long programId);
}

