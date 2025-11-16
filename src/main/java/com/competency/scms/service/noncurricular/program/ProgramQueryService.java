package com.competency.scms.service.noncurricular.program;

import com.competency.scms.dto.noncurricular.program.op.ProgramDetailDto;
import com.competency.scms.dto.noncurricular.program.op.ProgramListRowDto;
import com.competency.scms.dto.noncurricular.program.op.ProgramSearchCondDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProgramQueryService {

    ProgramDetailDto getDetailForOperator(Long operatorId, Long programId);
    Page<ProgramListRowDto> search(ProgramSearchCondDto cond, Pageable pageable);
}
