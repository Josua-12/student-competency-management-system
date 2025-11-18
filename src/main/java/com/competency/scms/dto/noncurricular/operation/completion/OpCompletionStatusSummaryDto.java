package com.competency.scms.dto.noncurricular.operation.completion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OpCompletionStatusSummaryDto {
    private long totalParticipants;   // 총 참가자 수
    private long completeCount;       // 이수 인원
    private long incompleteCount;     // 미이수/탈락 인원
    private double averageSatisfaction; // 평균 만족도 (없으면 0 또는 -1)
}


