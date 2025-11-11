package com.competency.scms.controller.counsel;

import com.competency.scms.dto.counsel.CounselingManagementDto;
import com.competency.scms.service.counsel.CounselingManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/counseling/management")
@RequiredArgsConstructor
public class CounselingManagementApiController {

    private final CounselingManagementService managementService;

    // CNSL-022: 상담분류관리
    @PostMapping("/categories")
    public ResponseEntity<Long> createCategory(@Valid @RequestBody CounselingManagementDto.CategoryRequest request) {
        Long categoryId = managementService.createCategory(request);
        return ResponseEntity.ok(categoryId);
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<Void> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CounselingManagementDto.CategoryRequest request) {
        managementService.updateCategory(categoryId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        managementService.deleteCategory(categoryId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CounselingManagementDto.CategoryResponse>> getAllCategories() {
        List<CounselingManagementDto.CategoryResponse> categories = managementService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // CNSL-023: 상담원관리
    @PostMapping("/counselors")
    public ResponseEntity<Void> createCounselor(@Valid @RequestBody CounselingManagementDto.CounselorRequest request) {
        managementService.createCounselor(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/counselors/{userId}")
    public ResponseEntity<Void> updateCounselor(
            @PathVariable Long userId,
            @Valid @RequestBody CounselingManagementDto.CounselorRequest request) {
        managementService.updateCounselor(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/counselors/{userId}")
    public ResponseEntity<Void> deleteCounselor(@PathVariable Long userId) {
        managementService.deleteCounselor(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/counselors")
    public ResponseEntity<Page<CounselingManagementDto.CounselorResponse>> getAllCounselors(Pageable pageable) {
        Page<CounselingManagementDto.CounselorResponse> counselors = managementService.getAllCounselors(pageable);
        return ResponseEntity.ok(counselors);
    }

    // CNSL-024: 상담만족도 문항관리
    @PostMapping("/questions")
    public ResponseEntity<Long> createQuestion(@Valid @RequestBody CounselingManagementDto.QuestionRequest request) {
        Long questionId = managementService.createQuestion(request);
        return ResponseEntity.ok(questionId);
    }

    @PutMapping("/questions/{questionId}")
    public ResponseEntity<Void> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody CounselingManagementDto.QuestionRequest request) {
        managementService.updateQuestion(questionId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        managementService.deleteQuestion(questionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/questions")
    public ResponseEntity<List<CounselingManagementDto.QuestionResponse>> getAllQuestions() {
        List<CounselingManagementDto.QuestionResponse> questions = managementService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }
}
