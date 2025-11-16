package com.competency.scms.controller.noncurricular.program;


import com.competency.scms.domain.noncurricular.program.Program;
import com.competency.scms.dto.noncurricular.program.op.ProgramOpenRequestDto;
import com.competency.scms.service.noncurricular.program.ProgramOpenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/noncurricular-operator/programs/{programId}/open")
public class ProgramOpenController {

    private final ProgramOpenService programOpenService;

    @GetMapping
    public ResponseEntity<?> load(@PathVariable Long programId) {
        Program p = programOpenService.load(programId);
        return ResponseEntity.ok(p); // 필요 시 DTO 변환 권장
    }

    @PostMapping(value = "/save", consumes = {"multipart/form-data"})
    public ResponseEntity<?> saveDraft(@PathVariable(required = false) Long programId,
                                       @Valid @RequestPart("payload") ProgramOpenRequestDto payload,
                                       @RequestPart(value = "poster", required = false) MultipartFile poster,
                                       @RequestPart(value = "guides", required = false) List<MultipartFile> guides,
                                       @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        Long id = programOpenService.saveDraft(programId, payload, poster, guides, attachments);
        return ResponseEntity.ok(id);
    }

    @PostMapping(value = "/submit", consumes = {"multipart/form-data"})
    public ResponseEntity<?> submit(@PathVariable(required = false) Long programId,
                                    @Valid @RequestPart("payload") ProgramOpenRequestDto payload,
                                    @RequestPart(value = "poster", required = false) MultipartFile poster,
                                    @RequestPart(value = "guides", required = false) List<MultipartFile> guides,
                                    @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        Long id = programOpenService.submitApproval(programId, payload, poster, guides, attachments);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteDraft(@PathVariable Long programId) {
        programOpenService.deleteDraft(programId);
        return ResponseEntity.noContent().build();
    }
}

