package com.competency.scms.repository.noncurricular.program;

import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.dto.noncurricular.program.op.ProgramListRowDto;
import com.competency.scms.dto.noncurricular.program.op.ProgramSearchCondDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ProgramRepositoryCustom {
//    ProgramDetailDto findDetailById(Long programId);
    Optional<Program> findDetailById(Long programId);
    Page<ProgramListRowDto> search(ProgramSearchCondDto cond, Pageable pageable);
}
