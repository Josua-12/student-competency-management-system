package com.competency.SCMS.repository.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.operation.ProgramSatisfaction;
import com.competency.SCMS.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ProgramSatisfactionRepository
        extends JpaRepository<ProgramSatisfaction, Long>, JpaSpecificationExecutor<ProgramSatisfaction> {

    // 프로그램 + 회차 + 학생(Long 컬럼)으로 단건 조회
    Optional<ProgramSatisfaction> findByProgram_IdAndSchedule_IdAndStudent(
            Long programId, Long scheduleId, User studentId);


    // 프로그램별 평균 평점
    @Query("""
        select avg(s.rating)
        from Satisfaction s
        where s.program.id = :programId
    """)
    Double findAverageRatingByProgram(@Param("programId") Long programId);

    // 회차별 평균 평점
    @Query("""
        select avg(s.rating)
        from Satisfaction s
        where s.schedule.id = :scheduleId
    """)
    Double findAverageRatingBySchedule(@Param("scheduleId") Long scheduleId);

    // 프로그램별 최신 만족도
    @Query("""
        select s
        from Satisfaction s
        where s.program.id = :programId
        order by s.createdAt desc
    """)
    List<ProgramSatisfaction> findRecentByProgram(@Param("programId") Long programId);

    // 학생별 1건 유니크
    Optional<ProgramSatisfaction> findByProgram_ProgramIdAndStudent_UserId(Long programId, Long studentId);

    // 프로그램 평균점수
    @Query("""
      select avg(s.rating) from ProgramSatisfaction s
      where s.program.programId = :programId
    """)
    Double getAverageRating(Long programId);

    Page<ProgramSatisfaction> findByProgram_ProgramId(Long programId, Pageable pageable);

}