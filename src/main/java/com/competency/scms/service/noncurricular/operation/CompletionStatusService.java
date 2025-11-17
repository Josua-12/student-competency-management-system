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
import java.time.LocalDateTime;
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
    public void updateCompletionStatus(Long applicationId,
                                       OpCompletionUpdateRequestDto request,
                                       Long operatorUserId) {

        ProgramApplication app = programApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("신청 정보가 존재하지 않습니다. id=" + applicationId));

        CompletionStatus status = request.getCompletionStatus();
        LocalDate completionDate = request.getCompletionDate(); // DTO는 LocalDate

        // 1) 상태가 COMPLETE 인데 날짜가 없으면 오늘 날짜로 기본 세팅
        if (status == CompletionStatus.COMPLETED && completionDate == null) {
            completionDate = LocalDate.now();
        }

        // 2) LocalDate -> LocalDateTime 변환
        LocalDateTime completionDateTime = null;
        if (completionDate != null) {
            // 하루의 시작 시각으로 세팅 (00:00:00)
            completionDateTime = completionDate.atStartOfDay();
        }

        // 3) 엔티티 세팅
        app.setCompletionStatus(status);
        app.setCompletionDate(completionDateTime);
    }

    /**
     * 다건 이수 상태 일괄 수정
     */
    @Transactional
    public void bulkUpdateCompletionStatus(OpBulkCompletionUpdateRequestDto request, Long operatorUserId) {

        // 방어코드
        if (request == null || request.getApplicationIds() == null || request.getApplicationIds().isEmpty()) {
            return;
        }

        // 요청에서 공통으로 쓸 값 꺼내기
        CompletionStatus status = request.getCompletionStatus();
        LocalDate completionDate = request.getCompletionDate();
        String remark = request.getRemark();

        // 상태가 COMPLETE(또는 COMPLETED)인데 날짜가 없으면 오늘 날짜로
        if ((status == CompletionStatus.COMPLETED /* 또는 COMPLETED */) && completionDate == null) {
            completionDate = LocalDate.now();
        }

        // LocalDate -> LocalDateTime 변환 (엔티티 필드 타입이 LocalDateTime 이라서)
        LocalDateTime completionDateTime = null;
        if (completionDate != null) {
            completionDateTime = completionDate.atStartOfDay();
        }

        // 선택된 신청건들 반복 처리
        for (Long applicationId : request.getApplicationIds()) {

            ProgramApplication app = programApplicationRepository.findById(applicationId)
                    .orElseThrow(() -> new IllegalArgumentException("신청 정보가 존재하지 않습니다. id=" + applicationId));

            // TODO: operatorUserId 기준 권한 체크 필요하면 여기에서

            app.setCompletionStatus(status);
            app.setCompletionDate(completionDateTime);
            // app.setCompletionRemark(remark); // 비고 필드가 있으면 사용

            // 이수증 여부 필드가 있으면 여기서 같이 조정
            // if (status == CompletionStatus.COMPLETE /* 또는 COMPLETED */) {
            //     app.setCertificateIssued(Boolean.TRUE);
            // } else {
            //     app.setCertificateIssued(Boolean.FALSE);
            // }
        }
    }


}

