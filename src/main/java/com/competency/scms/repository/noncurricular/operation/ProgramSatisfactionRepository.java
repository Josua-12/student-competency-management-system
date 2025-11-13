package com.competency.scms.repository.noncurricular.operation;

import com.competency.scms.domain.noncurricular.operation.ProgramSatisfaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ProgramSatisfactionRepository
        extends JpaRepository<ProgramSatisfaction, Long>, JpaSpecificationExecutor<ProgramSatisfaction> {

    /** 단건 조회: programId + scheduleId + studentId(값 타입) */
    Optional<ProgramSatisfaction> findByProgram_ProgramIdAndSchedule_ScheduleIdAndStudent_Id(
            Long programId, Long scheduleId, Long studentId);

    /** 목록 조회: programId + scheduleId (정렬 필드는 존재하는 필드로 선택) */
    List<ProgramSatisfaction> findAllByProgram_ProgramIdAndSchedule_ScheduleIdOrderByCreatedAtAsc(
            Long programId, Long scheduleId);

    /** 카운트(JPQL) — schedule.scheduleId 경로 사용 */
    @Query("""
        select count(ps)
          from ProgramSatisfaction ps
         where ps.program.programId = :programId
           and ps.schedule.scheduleId = :scheduleId
    """)
    long countByProgramAndSchedule(@Param("programId") Long programId,
                                   @Param("scheduleId") Long scheduleId);

    /** 통계용 원본 조회: programId + scheduleId */
    List<ProgramSatisfaction> findAllByProgram_ProgramIdAndSchedule_ScheduleId(
            Long programId, Long scheduleId);

    /** 프로그램별 평균 평점 */
    @Query("""
        select avg(s.rating)
          from ProgramSatisfaction s
         where s.program.programId = :programId
    """)
    Double findAverageRatingByProgram(@Param("programId") Long programId);

    /** 회차별 평균 평점 */
    @Query("""
        select avg(s.rating)
          from ProgramSatisfaction s
         where s.schedule.scheduleId = :scheduleId
    """)
    Double findAverageRatingBySchedule(@Param("scheduleId") Long scheduleId);
}
