package com.competency.scms.service.noncurricular.operation;

import com.competency.scms.domain.noncurricular.program.CompletionStatus;
import com.competency.scms.domain.noncurricular.operation.ProgramApplication;
import com.competency.scms.dto.noncurricular.operation.completion.*;
import com.competency.scms.repository.noncurricular.operation.ProgramApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompletionStatusService {

    private final ProgramApplicationRepository programApplicationRepository;

    /**
     * 이수현황 목록 + 요약 조회
     */
    public OpCompletionStatusListResultDto getCompletionStatusList(
            OpCompletionStatusSearchConditionDto condition,
            Long operatorDeptId,
            Pageable pageable
    ) {
        // Repository @Query 메서드 호출
        Page<OpCompletionStatusListItemDto> page =
                programApplicationRepository.searchCompletionStatusList(
                        condition.getProgramName(),
                        condition.getProgId(),            // progCode
                        condition.getCompletionStatus(),  // CompletionStatus
                        operatorDeptId,
                        pageable
                );

        List<OpCompletionStatusListItemDto> items = page.getContent();
        long total = page.getTotalElements();

        long completeCount = items.stream()
                .filter(i -> i.getCompletionStatus() == CompletionStatus.COMPLETED)
                .count();

        long incompleteCount = total - completeCount;

        double avgSatisfaction = items.stream()
                .filter(i -> Boolean.TRUE.equals(i.getSatisfactionSubmitted())
                        && i.getSatisfactionScore() != null)
                .mapToDouble(OpCompletionStatusListItemDto::getSatisfactionScore)
                .average()
                .orElse(0.0);

        OpCompletionStatusSummaryDto summary = OpCompletionStatusSummaryDto.builder()
                .totalParticipants(total)
                .completeCount(completeCount)
                .incompleteCount(incompleteCount)
                .averageSatisfaction(avgSatisfaction)
                .build();

        return OpCompletionStatusListResultDto.builder()
                .items(items)
                .totalCount(total)
                .summary(summary)
                .build();
    }

    /**
     * 단건 이수 상태 수정
     */
    @Transactional
    public void updateCompletionStatus(Long applicationId, OpCompletionUpdateRequestDto request, Long operatorUserId) {
        ProgramApplication app = programApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("신청 정보가 존재하지 않습니다. id=" + applicationId));

        // TODO: operatorUserId 기준으로 이 신청을 수정할 권한이 있는지 체크

        CompletionStatus status = request.getCompletionStatus();
        LocalDate completionDate = request.getCompletionDate();

        if (status == CompletionStatus.COMPLETED && completionDate == null) {
            completionDate = LocalDate.now();
        }

        app.setCompletionStatus(status);
        app.setCompletionDate(completionDate);
        // app.setCompletionRemark(request.getRemark()); // 비고 필드 있으면 사용

        if (status == CompletionStatus.COMPLETED) {
            app.setCertificateIssued(Boolean.TRUE);
        } else {
            app.setCertificateIssued(Boolean.FALSE);
        }
    }

    /**
     * 다건 이수 상태 일괄 수정
     */
    @Transactional
    public void bulkUpdateCompletionStatus(OpBulkCompletionUpdateRequestDto request, Long operatorUserId) {
        if (request.getApplicationIds() == null || request.getApplicationIds().isEmpty()) {
            return;
        }

        for (Long id : request.getApplicationIds()) {
            CompletionUpdateRequestDto single = new CompletionUpdateRequestDto();
            single.setCompletionStatus(request.getCompletionStatus());
            single.setCompletionDate(request.getCompletionDate());
            single.setRemark(request.getRemark());

            updateCompletionStatus(id, single, operatorUserId);
        }
    }
}

