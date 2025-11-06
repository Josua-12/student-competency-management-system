package com.competency.SCMS.repository.noncurricular.program;

import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.dto.noncurricular.program.ProgramDetailDto;
import com.competency.SCMS.dto.noncurricular.program.ProgramListRow;
import com.competency.SCMS.dto.noncurricular.program.ProgramSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ProgramRepositoryCustom {
//    ProgramDetailDto findDetailById(Long programId);
    Optional<Program> findDetailById(Long programId);
    Page<ProgramListRow> search(ProgramSearchCond cond, Pageable pageable);
}
