package com.competency.SCMS.repository.noncurricular.program;

import com.competency.SCMS.domain.noncurricular.program.Program;
import com.competency.SCMS.dto.noncurricular.program.ProgramListRowDto;
import com.competency.SCMS.dto.noncurricular.program.ProgramSearchCondDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ProgramRepositoryCustom {
//    ProgramDetailDto findDetailById(Long programId);
    Optional<Program> findDetailById(Long programId);
    Page<ProgramListRowDto> search(ProgramSearchCondDto cond, Pageable pageable);
}
