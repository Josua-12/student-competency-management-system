package com.competency.SCMS.service.noncurricular.program;

import com.competency.SCMS.dto.noncurricular.program.ProgramListRow;
import com.competency.SCMS.dto.noncurricular.program.ProgramSearchCond;
import com.competency.SCMS.repository.noncurricular.program.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProgramQueryServiceImpl implements ProgramQueryService {

    private final ProgramRepository programRepository;

    @Override
    public Page<ProgramListRow> search(ProgramSearchCond cond, Pageable pageable) {
        return programRepository.searchForOperatorList(cond, pageable);
    }
}