package com.competency.SCMS.repository.noncurricular.operation;

import com.competency.SCMS.domain.noncurricular.operation.Satisfaction;
import com.competency.SCMS.domain.user.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface SatisfactionRepository
        extends JpaRepository<Satisfaction, Long>, JpaSpecificationExecutor<Satisfaction> {

    // 프로그램 + 회차 + 학생(Long 컬럼)으로 단건 조회
    Optional<Satisfaction> findByProgram_IdAndSchedule_IdAndStudent(
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
    List<Satisfaction> findRecentByProgram(@Param("programId") Long programId);
}