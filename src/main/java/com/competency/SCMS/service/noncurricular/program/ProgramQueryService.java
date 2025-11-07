package com.competency.SCMS.service.noncurricular.program;

import com.competency.SCMS.dto.noncurricular.program.ProgramDetailDto;
import com.competency.SCMS.dto.noncurricular.program.ProgramListItemDto;
import com.competency.SCMS.dto.noncurricular.program.ProgramListRow;
import com.competency.SCMS.dto.noncurricular.program.ProgramSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProgramQueryService {

    ProgramDetailDto getDetailForOperator(Long operatorId, Long programId);
    Page<ProgramListItemDto> search(ProgramSearchCond cond, Pageable pageable);
}
