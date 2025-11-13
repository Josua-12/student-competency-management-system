package com.competency.scms.controller.counsel;

import com.competency.scms.domain.user.User;
import com.competency.scms.dto.counsel.CounselingRecordDto;
import com.competency.scms.service.counsel.CounselingRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/counseling/records")
@RequiredArgsConstructor
public class CounselingRecordApiController {

    private final CounselingRecordService recordService;

    // CNSL-012: 상담일지 작성
    @PostMapping
    public ResponseEntity<Long> createRecord(
            @Valid @RequestBody CounselingRecordDto.CreateRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        Long recordId = recordService.createRecord(request, currentUser);
        return ResponseEntity.ok(recordId);
    }

    // CNSL-012: 상담일지 수정
    @PutMapping("/{recordId}")
    public ResponseEntity<Void> updateRecord(
            @PathVariable Long recordId,
            @Valid @RequestBody CounselingRecordDto.UpdateRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        recordService.updateRecord(recordId, request, currentUser);
        return ResponseEntity.ok().build();
    }
    
    // CNSL-012: 상담일지 삭제
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(
            @PathVariable Long recordId,
            @AuthenticationPrincipal User currentUser) {
        
        recordService.deleteRecord(recordId, currentUser);
        return ResponseEntity.ok().build();
    }

    // CNSL-013: 상담일지 목록 조회
    @GetMapping
    public ResponseEntity<Page<CounselingRecordDto.ListResponse>> getRecordList(
            Pageable pageable,
            @AuthenticationPrincipal User currentUser) {
        
        Page<CounselingRecordDto.ListResponse> records = recordService.getRecordList(currentUser, pageable);
        return ResponseEntity.ok(records);
    }

    // CNSL-014: 상담일지 상세 조회
    @GetMapping("/{recordId}")
    public ResponseEntity<CounselingRecordDto.DetailResponse> getRecordDetail(
            @PathVariable Long recordId,
            @AuthenticationPrincipal User currentUser) {
        
        CounselingRecordDto.DetailResponse detail = recordService.getRecordDetail(recordId, currentUser);
        return ResponseEntity.ok(detail);
    }
}
