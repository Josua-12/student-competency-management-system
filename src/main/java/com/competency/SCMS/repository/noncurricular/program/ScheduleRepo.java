package com.competency.SCMS.repository.noncurricular.program;

import com.competency.SCMS.domain.noncurricular.program.ProgramSchedule;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface ScheduleRepo extends JpaRepository<ProgramSchedule, Long> {
    List<ProgramSchedule> findByProgramIdOrderBySessionNoAsc(Long programId);
    boolean existsByProgramIdAndSessionNo(Long programId, Integer sessionNo);
    void deleteByProgramId(Long programId);
}

