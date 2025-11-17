package com.competency.scms.service.noncurricular.operation;

import com.competency.scms.dto.noncurricular.operation.pending.ProgramBatchActionRequestDto;
import com.competency.scms.dto.noncurricular.operation.pending.ProgramBatchActionResultDto;
import com.competency.scms.dto.noncurricular.operation.pending.ProgramRejectRequestDto;
import com.competency.scms.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/noncurricular/admin/programs")
public class ProgramApprovalAdminApiController {

    private final ProgramApprovalService programApprovalService;

    /**
     * 단건 승인
     * POST /api/noncurricular/admin/programs/{progId}/approve
     */
    @PostMapping("/{progId}/approve")
    public ResponseEntity<Void> approveSingle(@PathVariable Long progId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {

        String approverUserNum = userDetails.getUser().getUserNum().toString();
        programApprovalService.approveSingle(progId, approverUserNum);
        return ResponseEntity.ok().build();
    }

    /**
     * 단건 반려
     * POST /api/noncurricular/admin/programs/{progId}/reject
     */
    @PostMapping("/{progId}/reject")
    public ResponseEntity<Void> rejectSingle(@PathVariable Long progId,
                                             @RequestBody ProgramRejectRequestDto request,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {

        String approverUserNum = userDetails.getUser().getUserNum().toString();
        programApprovalService.rejectSingle(progId, request.getReason(), approverUserNum);
        return ResponseEntity.ok().build();
    }

    /**
     * 일괄 승인
     * POST /api/noncurricular/admin/programs/approve-batch
     */
    @PostMapping("/approve-batch")
    public ResponseEntity<ProgramBatchActionResultDto> approveBatch(
            @RequestBody ProgramBatchActionRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String approverUserNum = userDetails.getUser().getUserNum().toString();
        ProgramBatchActionResultDto result = programApprovalService.approveBatch(
                request.getProgIds(), approverUserNum);

        return ResponseEntity.ok(result);
    }

    /**
     * 일괄 반려
     * POST /api/noncurricular/admin/programs/reject-batch
     */
    @PostMapping("/reject-batch")
    public ResponseEntity<ProgramBatchActionResultDto> rejectBatch(
            @RequestBody ProgramBatchActionRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String approverUserNum = userDetails.getUser().getUserNum().toString();
        ProgramBatchActionResultDto result = programApprovalService.rejectBatch(
                request.getProgIds(),
                request.getReason(),
                approverUserNum
        );

        return ResponseEntity.ok(result);
    }

    /**
     * 엑셀 다운로드 (Stub)
     * GET /api/noncurricular/admin/programs/pending/export
     * → 나중에 실제 엑셀 생성 로직 붙이면 됨
     */
    @GetMapping("/pending/export")
    public ResponseEntity<Void> exportPendingPrograms() {
        // TODO: 엑셀 생성 & 파일 반환
        return ResponseEntity.noContent().build();
    }
}

