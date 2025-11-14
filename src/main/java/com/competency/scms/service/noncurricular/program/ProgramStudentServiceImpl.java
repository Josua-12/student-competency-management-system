package com.competency.scms.service.noncurricular.program;

import com.competency.scms.domain.noncurricular.operation.ApplicationStatus;
import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.noncurricular.operation.ApprovalStatus;
import com.competency.scms.dto.noncurricular.program.student.ProgramStudentListResponse;
import com.competency.scms.dto.noncurricular.program.student.ProgramStudentSearchRequest;
import com.competency.scms.repository.noncurricular.operation.ProgramApplicationRepository;
import com.competency.scms.repository.noncurricular.program.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramStudentServiceImpl implements ProgramStudentService {

    private final ProgramRepository programRepository;
    private final ProgramApplicationRepository programApplicationRepository;

    @Override
    public Page<ProgramStudentListResponse> getProgramListForStudent(ProgramStudentSearchRequest condition,
                                                                     Pageable pageable) {

        Page<Program> page = programRepository.searchStudentPrograms(
                condition.getKeyword(),
                condition.getCategory(),
                condition.getStatus(),
                condition.getDept(),
                condition.getFrom(),
                condition.getTo(),
                pageable
        );

        return page.map(program -> {
            // 승인된 신청 건수만 집계하는 예시
            int appliedCount = (int) programApplicationRepository
                    .countByProgram_ProgramIdAndStatus(program.getProgramId(), ApplicationStatus.APPROVED);

            return ProgramStudentListResponse.from(program, appliedCount);
        });
    }
}

