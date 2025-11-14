package com.competency.scms.controller.counsel;

import com.competency.scms.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/counseling/written-editing")
@RequiredArgsConstructor
public class WrittenEditingApiController {

    @PostMapping
    public ResponseEntity<Long> submitWrittenEditing(
            @RequestParam String editingType,
            @RequestParam String companyName,
            @RequestParam String jobPosition,
            @RequestParam String recruitmentStage,
            @RequestParam String recruitmentType,
            @RequestParam String requestContent,
            @RequestParam(required = false) MultipartFile file,
            @AuthenticationPrincipal User currentUser) {
        
        // TODO: 서면첨삭 신청 로직 구현
        // 1. 파일 저장
        // 2. CounselingReservation 생성 (서면첨삭 타입)
        // 3. 반환
        
        return ResponseEntity.ok(1L);
    }
}
