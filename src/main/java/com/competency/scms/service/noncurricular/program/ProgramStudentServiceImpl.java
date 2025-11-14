package com.competency.scms.service.noncurricular.program;

import com.competency.scms.domain.noncurricular.operation.ApplicationStatus;
import com.competency.scms.domain.noncurricular.operation.ApprovalStatus;
import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.dto.noncurricular.program.student.ProgramStudentListResponse;
import com.competency.scms.dto.noncurricular.program.student.ProgramStudentSearchRequest;
import com.competency.scms.repository.noncurricular.operation.ProgramApplicationRepository;
import com.competency.scms.repository.noncurricular.program.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramStudentServiceImpl implements ProgramStudentService {

    private final ProgramRepository programRepository;
    private final ProgramApplicationRepository programApplicationRepository;

    @Override
    public Page<ProgramStudentListResponse> getProgramListForStudent(ProgramStudentSearchRequest condition,
                                                                     Pageable pageable) {

        // LocalDate → LocalDateTime 변환 (하루 시작/끝 기준)
        LocalDateTime fromDateTime = toStartOfDay(condition.getFrom());
        LocalDateTime toDateTime   = toEndOfDay(condition.getTo());

        Page<Program> page = programRepository.searchStudentPrograms(
                condition.getKeyword(),
                condition.getCategory(),
                condition.getStatus(),
                condition.getDept(),
                fromDateTime,
                toDateTime,
                ApprovalStatus.APPROVED,   // 학생에게는 승인된 프로그램만 노출
                pageable
        );

        return page.map(program -> {
            // 승인된 신청 건수 카운트
            int appliedCount = (int) programApplicationRepository
                    .countByProgram_ProgramIdAndStatus(program.getProgramId(), ApplicationStatus.APPROVED);

            return ProgramStudentListResponse.from(program, appliedCount);
        });
    }

    private LocalDateTime toStartOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    private LocalDateTime toEndOfDay(LocalDate date) {
        return date == null ? null : date.atTime(23, 59, 59);
    }
}


