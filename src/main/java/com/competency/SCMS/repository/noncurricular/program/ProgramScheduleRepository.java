package com.competency.SCMS.repository.noncurricular.program;

import com.competency.SCMS.domain.noncurricular.program.ProgramSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.*;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.*;

public interface ProgramScheduleRepository extends JpaRepository<ProgramSchedule, Long> {
    List<ProgramSchedule> findByProgram_ProgramIdOrderByStartDateTimeAsc(Long programId);
    Page<ProgramSchedule> findByProgram_ProgramId(Long programId, Pageable pageable);
    List<ProgramSchedule> findAllByProgram_IdOrderByRoundNoAsc(Long programId);
    // 특정 기간 내 회차
    List<ProgramSchedule> findByStartDateTimeBetween(LocalDateTime from, LocalDateTime to);
}

