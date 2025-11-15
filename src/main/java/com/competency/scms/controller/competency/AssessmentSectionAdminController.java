package com.competency.scms.controller.competency;

import com.competency.scms.dto.competency.AssessmentSectionFormDto;
import com.competency.scms.service.competency.AssessmentSectionAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/competency-admin/assessment-section")
@RequiredArgsConstructor
public class AssessmentSectionAdminController {

    private final AssessmentSectionAdminService sectionAdminService;

    // 1. 관리 페이지 반환
    @GetMapping
    public String sectionAdminPage() {
        return "competency/admin-section";
    }

    // 2. API: 모든 섹션 목록 조회
    @GetMapping("/api/sections")
    @ResponseBody
    public ResponseEntity<List<AssessmentSectionFormDto>> getAllSections() {
        List<AssessmentSectionFormDto> sections = sectionAdminService.getAllSections();
        return ResponseEntity.ok(sections);
    }

    // 3. API: 섹션 상세 조회
    @GetMapping("/api/sections/{id}")
    @ResponseBody
    public ResponseEntity<AssessmentSectionFormDto> getSectionDetails(@PathVariable("id") Long id) {
        AssessmentSectionFormDto dto = sectionAdminService.getSectionDetails(id);
        return ResponseEntity.ok(dto);
    }

    // 4. API: 섹션 저장/수정
    @PostMapping("/api/sections")
    @ResponseBody
    public ResponseEntity<?> saveOrUpdateSection(@RequestBody AssessmentSectionFormDto dto, Map map) {
        try {
            Long savedId = sectionAdminService.saveOrUpdateSection(dto);
            return ResponseEntity.ok(Map.of("message", "저장되었습니다.", "id", savedId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 5. API: 섹션 삭제
    @DeleteMapping("/api/sections/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteSection(@PathVariable("id") Long id) {
        try {
            sectionAdminService.deleteSection(id);
            return ResponseEntity.ok(Map.of("message", "삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
