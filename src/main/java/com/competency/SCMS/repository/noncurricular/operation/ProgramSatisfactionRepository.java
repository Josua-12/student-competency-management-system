package com.competency.SCMS.repository.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.operation.ProgramSatisfaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ProgramSatisfactionRepository
        extends JpaRepository<ProgramSatisfaction, Long>, JpaSpecificationExecutor<ProgramSatisfaction> {

    // 파생 메서드: 엔티티 경로를 명시적으로 풀어 Long 키들로 매칭
    Optional<ProgramSatisfaction> findByProgram_ProgramIdAndSchedule_ScheduleIdAndStudent_UserId(
            Long programId, Long scheduleId, Long studentId
    );

    // 프로그램별 평균 평점
    @Query("""
        select avg(s.rating)
        from ProgramSatisfaction s
        where s.program.programId = :programId
    """)
    Double findAverageRatingByProgram(@Param("programId") Long programId);

    // 회차별 평균 평점
    @Query("""
        select avg(s.rating)
        from ProgramSatisfaction s
        where s.schedule.scheduleId = :scheduleId
    """)
    Double findAverageRatingBySchedule(@Param("scheduleId") Long scheduleId);
}
