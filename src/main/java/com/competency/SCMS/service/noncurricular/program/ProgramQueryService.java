package com.competency.SCMS.service.noncurricular.program;

import com.competency.SCMS.dto.noncurricular.program.ProgramDetailDto;
import com.competency.SCMS.dto.noncurricular.program.ProgramListRowDto;
import com.competency.SCMS.dto.noncurricular.program.ProgramSearchCondDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProgramQueryService {

    ProgramDetailDto getDetailForOperator(Long operatorId, Long programId);
    Page<ProgramListRowDto> search(ProgramSearchCondDto cond, Pageable pageable);
}
