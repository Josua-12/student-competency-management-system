package com.competency.scms.controller.noncurricular.operation;

import com.competency.scms.dto.noncurricular.operation.completion.*;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.noncurricular.operation.CompletionStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/noncurricular/completions")
public class CompletionStatusApiController {

    private final CompletionStatusService completionStatusService;

    /**
     * 이수현황 목록 + 요약 조회
     * GET /api/noncurricular/completions
     */
    @GetMapping
    public ResponseEntity<OpCompletionStatusListResultDto> getCompletionStatusList(
            OpCompletionStatusSearchConditionDto condition,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        // 예시: 운영자는 자신의 부서 ID를 필터링에 사용
        Long operatorDeptId = user.getId(); // TODO: 실제 CustomUserDetails 구조에 맞게 수정

        OpCompletionStatusListResultDto result =
                completionStatusService.getCompletionStatusList(condition, operatorDeptId, pageable);

        return ResponseEntity.ok(result);
    }

    /**
     * 단건 이수 상태 변경
     * POST /api/noncurricular/completions/{applicationId}/complete
     */
    @PostMapping("/{applicationId}/complete")
    public ResponseEntity<Void> updateCompletionStatus(
            @PathVariable Long applicationId,
            @RequestBody OpCompletionUpdateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        completionStatusService.updateCompletionStatus(applicationId, request, user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * 일괄 이수/미이수/탈락 처리
     * POST /api/noncurricular/completions/bulk-complete
     */
    @PostMapping("/bulk-complete")
    public ResponseEntity<Void> bulkUpdateCompletionStatus(
            @RequestBody OpBulkCompletionUpdateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        completionStatusService.bulkUpdateCompletionStatus(request, user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * 엑셀 다운로드
     * GET /api/noncurricular/completions/export
     *
     * (지금은 스켈레톤만, 나중에 실제 엑셀 생성로직 붙이면 됨)
     */
    @GetMapping("/export")
    public ResponseEntity<Void> exportCompletionStatus(
            OpCompletionStatusSearchConditionDto condition,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        // TODO: 엑셀 생성 서비스 구현 후 ResponseEntity<Resource> 로 변경
        return ResponseEntity.status(501).build(); // Not Implemented
    }
}

