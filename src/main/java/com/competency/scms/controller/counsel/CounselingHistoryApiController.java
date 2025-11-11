package com.competency.scms.controller.counsel;

import com.competency.scms.domain.user.User;
import com.competency.scms.dto.counsel.CounselingHistoryDto;
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
            @AuthenticationPrincipal User currentUser) {
        
        Page<CounselingHistoryDto.HistoryResponse> history = 
                historyService.getAllHistory(condition, currentUser, pageable);
        return ResponseEntity.ok(history);
    }

    // CNSL-016: 상담사별 상담 이력 조회
    @GetMapping("/counselor")
    public ResponseEntity<Page<CounselingHistoryDto.HistoryResponse>> getCounselorHistory(
            Pageable pageable,
            @AuthenticationPrincipal User currentUser) {
        
        Page<CounselingHistoryDto.HistoryResponse> history = 
                historyService.getCounselorHistory(currentUser, pageable);
        return ResponseEntity.ok(history);
    }

    // CNSL-017: 상담사 본인 담당 상담 현황
    @GetMapping("/status")
    public ResponseEntity<CounselingHistoryDto.StatusResponse> getCounselorStatus(
            @AuthenticationPrincipal User currentUser) {
        
        CounselingHistoryDto.StatusResponse status = historyService.getCounselorStatus(currentUser);
        return ResponseEntity.ok(status);
    }
}
