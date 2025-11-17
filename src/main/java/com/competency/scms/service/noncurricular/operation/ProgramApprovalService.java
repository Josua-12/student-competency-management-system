package com.competency.scms.service.noncurricular.operation;
import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.domain.noncurricular.program.ProgramStatus;
import com.competency.scms.dto.noncurricular.operation.pending.ProgramBatchActionResultDto;
import com.competency.scms.dto.noncurricular.operation.pending.ProgramPendingListItemDto;
import com.competency.scms.dto.noncurricular.operation.pending.ProgramPendingListResultDto;
import com.competency.scms.dto.noncurricular.operation.pending.ProgramPendingSearchConditionDto;
import com.competency.scms.repository.noncurricular.program.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramApprovalService {

    private final ProgramRepository programRepository;

    /**
     * 승인대기/승인/반려 등 프로그램 목록 조회
     */
    @Transactional(readOnly = true)
    public ProgramPendingListResultDto getPendingPrograms(ProgramPendingSearchConditionDto condition,
                                                          Pageable pageable) {

        ProgramStatus status = null;
        if (condition.getApprovalStatus() != null && !condition.getApprovalStatus().isBlank()) {
            status = ProgramStatus.valueOf(condition.getApprovalStatus());
        }

        Page<Program> page = programRepository.searchProgramsForApproval(
                condition.getProgramTitle(),
                condition.getDepartmentId(),
                status,
                condition.getRequestDateFrom(),
                condition.getRequestDateTo(),
                condition.getAppStartFrom(),
                condition.getAppEndTo(),
                pageable
        );

        Page<ProgramPendingListItemDto> dtoPage = page.map(p ->
                ProgramPendingListItemDto.builder()
                        .progId(p.getProgramId())
                        .title(p.getTitle())
                        .categoryName(p.getCategory() != null ? p.getCategory().getLabel() : null)
                        .departmentName(p.getDepartment() != null ? p.getDepartment().getName() : null)
                        .status(p.getStatus().name())
                        .requestDate(p.getUpdatedAt())
                        .appStart(p.getProgramStartAt())
                        .appEnd(p.getProgramEndAt())
                        .maxPart(p.getMaxParticipants())
                        .curPart(p.getCurrentParticipants())
                        .build()
        );

        return ProgramPendingListResultDto.fromPage(dtoPage);
    }

    /**
     * 단건 승인
     */
    public void approveSingle(Long progId, String approverUserNum) {
        Program program = programRepository.findById(progId)
                .orElseThrow(() -> new IllegalArgumentException("프로그램을 찾을 수 없습니다. progId=" + progId));

        if (program.getStatus() != ProgramStatus.PENDING) {
            throw new IllegalStateException("대기 상태의 프로그램만 승인할 수 있습니다.");
        }

        program.setStatus(ProgramStatus.APPROVED);
        // TODO: 승인 이력 기록(approverUserNum, 일시 등)
    }

    /**
     * 단건 반려
     */
    public void rejectSingle(Long progId, String reason, String approverUserNum) {
        Program program = programRepository.findById(progId)
                .orElseThrow(() -> new IllegalArgumentException("프로그램을 찾을 수 없습니다. progId=" + progId));

        if (program.getStatus() != ProgramStatus.PENDING) {
            throw new IllegalStateException("대기 상태의 프로그램만 반려할 수 있습니다.");
        }

        program.setStatus(ProgramStatus.REJECTED);
        // TODO: 반려 이력(사유, approverUserNum) 기록
    }

    /**
     * 일괄 승인
     */
    public ProgramBatchActionResultDto approveBatch(List<Long> progIds, String approverUserNum) {
        int success = 0;
        int fail = 0;

        for (Long progId : progIds) {
            try {
                approveSingle(progId, approverUserNum);
                success++;
            } catch (Exception e) {
                fail++;
            }
        }

        return ProgramBatchActionResultDto.builder()
                .successCount(success)
                .failCount(fail)
                .build();
    }

    /**
     * 일괄 반려
     */
    public ProgramBatchActionResultDto rejectBatch(List<Long> progIds, String reason, String approverUserNum) {
        int success = 0;
        int fail = 0;

        for (Long progId : progIds) {
            try {
                rejectSingle(progId, reason, approverUserNum);
                success++;
            } catch (Exception e) {
                fail++;
            }
        }

        return ProgramBatchActionResultDto.builder()
                .successCount(success)
                .failCount(fail)
                .build();
    }
}

