package com.competency.SCMS.service.noncurricular.operation;

import com.competency.SCMS.dto.noncurricular.operation.NotifyRequestDto;
import com.competency.SCMS.dto.noncurricular.operation.ParticipantPageResponseDto;
import com.competency.SCMS.dto.noncurricular.operation.ParticipantSearchConditionDto;
import org.springframework.data.domain.Pageable;


public interface  ProgramParticipantService {
    ParticipantPageResponseDto search(Long programId, ParticipantSearchConditionDto cond, Pageable pageable);

    void approve(Long programId, Long applicationId, String reason);

    void reject(Long programId, Long applicationId, String reason);

    void cancel(Long programId, Long applicationId, String reason);

    void notifyToApplicants(Long programId, NotifyRequestDto req);

    byte[] exportExcel(Long programId, ParticipantSearchConditionDto cond); // 간단히 바이트 배열 반환
}
