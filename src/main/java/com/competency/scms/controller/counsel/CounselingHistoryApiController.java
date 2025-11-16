package com.competency.scms.controller.counsel;

import com.competency.scms.dto.counsel.CounselingHistoryDto;
import com.competency.scms.security.CustomUserDetails;
import com.competency.scms.service.counsel.CounselingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/counseling/history")
@RequiredArgsConstructor
public class CounselingHistoryApiController {

    private final CounselingHistoryService historyService;

    // CNSL-015: 전체 상담 이력 조회
    @GetMapping
    public ResponseEntity<Page<CounselingHistoryDto.HistoryResponse>> getAllHistory(
            @ModelAttribute CounselingHistoryDto.SearchCondition condition,
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Page<CounselingHistoryDto.HistoryResponse> history = 
                historyService.getAllHistory(condition, userDetails.getUser(), pageable);
        return ResponseEntity.ok(history);
    }

    // CNSL-016: 상담사별 상담 이력 조회
    @GetMapping("/counselor")
    public ResponseEntity<Page<CounselingHistoryDto.HistoryResponse>> getCounselorHistory(
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Page<CounselingHistoryDto.HistoryResponse> history = 
                historyService.getCounselorHistory(userDetails.getUser(), pageable);
        return ResponseEntity.ok(history);
    }

    // CNSL-017: 상담사 본인 담당 상담 현황
    @GetMapping("/status")
    public ResponseEntity<CounselingHistoryDto.StatusResponse> getCounselorStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CounselingHistoryDto.StatusResponse status = historyService.getCounselorStatus(userDetails.getUser());
        return ResponseEntity.ok(status);
    }
}
