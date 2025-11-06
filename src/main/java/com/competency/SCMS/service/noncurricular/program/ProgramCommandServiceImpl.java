package com.competency.SCMS.service.noncurricular.program;

import com.competency.SCMS.domain.noncurricular.program.Program;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramCommandServiceImpl implements ProgramCommandService {

    private final com.competency.SCMS.repository.noncurricular.program.ProgramRepository programRepository;

    @Override
    public void requestApproval(Long programId) {
        Program p = programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found: " + programId));
        p.requestApproval();
    }

    @Override
    public void requestApproval(List<Long> programIds) {
        for(Long id : programIds){ requestApproval(id); }
    }

    @Override
    public void delete(Long programId) {
        programRepository.deleteById(programId);
    }

    @Override
    public void delete(List<Long> programIds) {
        for(Long id : programIds){ programRepository.deleteById(id); }
    }
}

