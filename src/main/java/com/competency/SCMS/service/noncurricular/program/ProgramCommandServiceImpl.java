package com.competency.SCMS.service.noncurricular.program;

import com.competency.SCMS.domain.noncurricular.operation.ApprovalStatus;
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

    @Override
    public void update(Long programId, Object updateCommand, Long operatorId) {
        Program p = programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found: " + programId));
        // TODO: 권한 체크
        // TODO: p.update(updateCommand);
        // JPA flush by tx
    }

    @Override
    public void requestApproval(Long programId, Long operatorId) {
        Program p = programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found: " + programId));
        // TODO: 권한 체크
        // 상태 전이 예시
        p.setApprovalStatus(ApprovalStatus.REQ); // 엔티티에 setter 또는 도메인 메서드 필요
        // 승인 이력 추가는 별 Repository or 도메인 이벤트로
    }

    @Override
    public void delete(Long programId, Long operatorId) {
        Program p = programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found: " + programId));
        // TODO: 권한 체크
        programRepository.delete(p);
    }
}

