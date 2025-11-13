package com.competency.scms.repository.noncurricular.program;

import com.competency.scms.domain.noncurricular.program.ProgramSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public interface ProgramScheduleRepository extends JpaRepository<ProgramSchedule, Long> {
    // 프로그램 별 회차 목록: (일자→시작시간) 오름차순
    List<ProgramSchedule> findByProgram_ProgramIdOrderByDateAscStartTimeAsc(Long programId);

    // 페이징 조회 (정렬은 Pageable의 Sort로)
    Page<ProgramSchedule> findByProgram_ProgramId(Long programId, Pageable pageable);

    // 회차번호 오름차순
    List<ProgramSchedule> findAllByProgram_ProgramIdOrderBySessionNoAsc(Long programId);

    //  특정 "기간" 내 회차: 날짜 기준
    List<ProgramSchedule> findByDateBetween(LocalDate from, LocalDate to);

    // 특정 "구간" 내 회차: 날짜+시간까지 좁히고 싶을 때 (JPQL로 명시)
    @Query("""
        select s
        from ProgramSchedule s
        where 
            (s.date > :fromDate and s.date < :toDate)
         or (s.date = :fromDate and s.startTime >= :fromTime)
         or (s.date = :toDate   and s.startTime <= :toTime)
    """)
    List<ProgramSchedule> findByStartRange(
            @Param("fromDate") LocalDate fromDate,
            @Param("fromTime") LocalTime fromTime,
            @Param("toDate")   LocalDate toDate,
            @Param("toTime")   LocalTime toTime
    );

    List<ProgramSchedule> findAllByProgram_ProgIdOrderByStartDateAsc(Long progId);
}

//    List<ProgramSchedule> findByProgram_ProgramIdOrderByStartDateTimeAsc(Long programId);
//    Page<ProgramSchedule> findByProgram_ProgramId(Long programId, Pageable pageable);
//    List<ProgramSchedule> findAllByProgram_ProgramIdOrderBySessionNoAsc(Long programId);
//    // 특정 기간 내 회차
//    List<ProgramSchedule> findByStartDateTimeBetween(LocalDateTime from, LocalDateTime to);


